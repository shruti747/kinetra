<?php
require_once __DIR__ . '/../config/config.php';

$error = '';
$name = '';
$email = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $name = trim($_POST['name'] ?? '');
    $email = strtolower(trim($_POST['email'] ?? ''));
    $password = $_POST['password'] ?? '';
    $confirm = $_POST['confirm_password'] ?? '';
    $token = $_POST['csrf_token'] ?? '';

    if (!hash_equals($_SESSION['csrf_token'], $token)) {
        $error = 'Invalid request. Please refresh and try again.';
    } elseif ($name === '' || strlen($name) > 100) {
        $error = 'Please enter your name.';
    } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL) || strlen($email) > 255) {
        $error = 'Please enter a valid email address.';
    } elseif (strlen($password) < 8) {
        $error = 'Password must be at least 8 characters.';
    } elseif ($password !== $confirm) {
        $error = 'Passwords do not match.';
    } else {
        $check = $pdo->prepare('SELECT id FROM users WHERE email = ?');
        $check->execute([$email]);

        if ($check->fetch()) {
            $error = 'An account with this email already exists.';
        } else {
            $hash = password_hash($password, PASSWORD_DEFAULT);

            $stmt = $pdo->prepare(
                'INSERT INTO users (name, email, password) VALUES (?, ?, ?)'
            );
            $stmt->execute([$name, $email, $hash]);

            session_regenerate_id(true);
            $_SESSION['user_id'] = (int) $pdo->lastInsertId();
            $_SESSION['user_name'] = $name;

            header('Location: ../index.php');
            exit;
        }
    }
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Create Account | Kinetra</title>
<style>
*{box-sizing:border-box}
body{margin:0;min-height:100vh;background:#080D18;color:#F7F9FC;
font-family:Arial,sans-serif;display:grid;place-items:center;padding:24px}
.panel{width:100%;max-width:430px;background:#10182B;border:1px solid #26334A;
border-radius:20px;padding:32px}
.logo{font-size:27px;font-weight:900;letter-spacing:4px}
.logo span{color:#52D9FF}
.subtitle{color:#8492A8;line-height:1.5}
label{display:block;margin:18px 0 8px;font-size:14px}
input{width:100%;padding:13px;border-radius:9px;border:1px solid #34425C;
background:#080D18;color:white;font-size:15px}
button{width:100%;margin-top:22px;padding:14px;border:0;border-radius:9px;
background:#356BFF;color:white;font-size:16px;font-weight:bold;cursor:pointer}
a{color:#52D9FF;text-decoration:none}
.error{background:#4A202B;padding:12px;border-radius:8px;color:#FFDCE2}
</style>
</head>
<body>
<main class="panel">
  <div class="logo">KINETRA<span>.</span></div>
  <h1>Create your account</h1>
  <p class="subtitle">Start tracking your progress and chasing your personal best.</p>

  <?php if ($error): ?>
    <p class="error"><?= htmlspecialchars($error, ENT_QUOTES, 'UTF-8') ?></p>
  <?php endif; ?>

  <form method="post" action="signup.php">
    <input type="hidden" name="csrf_token"
      value="<?= htmlspecialchars($_SESSION['csrf_token'], ENT_QUOTES, 'UTF-8') ?>">

    <label for="name">Full name</label>
    <input id="name" name="name" maxlength="100" required
      autocomplete="name"
      value="<?= htmlspecialchars($name, ENT_QUOTES, 'UTF-8') ?>">

    <label for="email">Email address</label>
    <input id="email" type="email" name="email" maxlength="255" required
      autocomplete="email"
      value="<?= htmlspecialchars($email, ENT_QUOTES, 'UTF-8') ?>">

    <label for="password">Password (at least 8 characters)</label>
    <input id="password" type="password" name="password" minlength="8"
      required autocomplete="new-password">

    <label for="confirm_password">Confirm password</label>
    <input id="confirm_password" type="password" name="confirm_password"
      minlength="8" required autocomplete="new-password">

    <button type="submit">Create account</button>
  </form>

  <p class="subtitle">Already registered? <a href="login.php">Sign in</a></p>
</main>
</body>
</html>