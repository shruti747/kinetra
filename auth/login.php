
<?php
require_once __DIR__ . '/../config/config.php';

$error = '';
$email = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $email = strtolower(trim($_POST['email'] ?? ''));
    $password = $_POST['password'] ?? '';
    $token = $_POST['csrf_token'] ?? '';

    if (
        !isset($_SESSION['csrf_token']) ||
        !is_string($token) ||
        !hash_equals($_SESSION['csrf_token'], $token)
    ) {
        $error = 'Invalid request. Please refresh and try again.';
    } else {
        $stmt = $pdo->prepare(
            'SELECT id, name, email, password FROM users WHERE email = ?'
        );
        $stmt->execute([$email]);
        $user = $stmt->fetch();

        if ($user && password_verify($password, $user['password'])) {
            session_regenerate_id(true);

            $_SESSION['user_id'] = (int) $user['id'];
            $_SESSION['user_name'] = $user['name'];

            header('Location: ../profile/index.php');
            exit;
        }

        $error = 'Incorrect email or password.';
    }
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Sign In | Kinetra</title>

<style>
* {
    box-sizing: border-box;
}

body {
    margin: 0;
    min-height: 100vh;
    background: #080D18;
    color: #F7F9FC;
    font-family: Arial, sans-serif;
    display: grid;
    place-items: center;
    padding: 24px;
}

.panel {
    width: 100%;
    max-width: 430px;
    background: #10182B;
    border: 1px solid #26334A;
    border-radius: 20px;
    padding: 32px;
}

.logo {
    font-size: 27px;
    font-weight: 900;
    letter-spacing: 4px;
}

.logo span {
    color: #52D9FF;
}

.subtitle {
    color: #8492A8;
    line-height: 1.6;
}

label {
    display: block;
    margin: 18px 0 8px;
    font-size: 14px;
}

input {
    width: 100%;
    padding: 13px;
    border-radius: 9px;
    border: 1px solid #34425C;
    background: #080D18;
    color: white;
    font-size: 15px;
}

button {
    width: 100%;
    margin-top: 22px;
    padding: 14px;
    border: 0;
    border-radius: 9px;
    background: #356BFF;
    color: white;
    font-size: 16px;
    font-weight: bold;
    cursor: pointer;
}

a {
    color: #52D9FF;
    text-decoration: none;
}

.error {
    background: #4A202B;
    padding: 12px;
    border-radius: 8px;
    color: #FFDCE2;
}
</style>
</head>

<body>

<main class="panel">

    <div class="logo">KINETRA<span>.</span></div>

    <h1>Welcome back</h1>

    <p class="subtitle">
        Sign in and keep moving towards your next personal best.
    </p>

    <?php if ($error): ?>
        <p class="error">
            <?= htmlspecialchars($error, ENT_QUOTES, 'UTF-8') ?>
        </p>
    <?php endif; ?>

    <form method="post" action="login.php">

        <input
            type="hidden"
            name="csrf_token"
            value="<?= htmlspecialchars($_SESSION['csrf_token'], ENT_QUOTES, 'UTF-8') ?>"
        >

        <label for="email">Email address</label>

        <input
            id="email"
            type="email"
            name="email"
            maxlength="255"
            autocomplete="email"
            required
            value="<?= htmlspecialchars($email, ENT_QUOTES, 'UTF-8') ?>"
        >

        <label for="password">Password</label>

        <input
            id="password"
            type="password"
            name="password"
            autocomplete="current-password"
            required
        >

        <button type="submit">Sign in</button>

    </form>

    <p class="subtitle">
        New to Kinetra?
        <a href="signup.php">Create an account</a>
    </p>

</main>

</body>
</html>
