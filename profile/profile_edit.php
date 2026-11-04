
<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';
requireLogin();

$userId = (int) $_SESSION['user_id'];
$error = '';

$stmt = $pdo->prepare(
    'SELECT name, email FROM users WHERE id = ?'
);
$stmt->execute([$userId]);
$user = $stmt->fetch();

if (!$user) {
    http_response_code(404);
    exit('Athlete profile not found.');
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    verifyCsrf();

    $name = trim($_POST['name'] ?? '');
    $email = trim($_POST['email'] ?? '');

    if ($name === '' || strlen($name) > 100) {
        $error = 'Please enter a name of up to 100 characters.';
    } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $error = 'Please enter a valid email address.';
    } elseif (strlen($email) > 255) {
        $error = 'Email address is too long.';
    } else {
        try {
            $stmt = $pdo->prepare(
                'UPDATE users SET name = ?, email = ? WHERE id = ?'
            );
            $stmt->execute([$name, $email, $userId]);

            header('Location: profile_fetch.php?updated=1');
            exit;
        } catch (PDOException $e) {
            if ($e->getCode() === '23000') {
                $error = 'This email address is already registered.';
            } else {
                error_log($e->getMessage());
                $error = 'Unable to update your profile right now.';
            }
        }
    }
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Kinetra | Edit Profile</title>
    <style>
        body {
            margin: 0;
            padding: 30px 16px;
            font-family: Arial, sans-serif;
            background: #0b1020;
            color: #f4f7ff;
        }
        .container {
            max-width: 500px;
            margin: 30px auto;
            padding: 28px;
            background: #141f35;
            border: 1px solid #293956;
            border-radius: 14px;
        }
        h1 { color: #55c8ff; }
        label { display: block; margin: 18px 0 8px; }
        input {
            width: 100%;
            padding: 12px;
            box-sizing: border-box;
            border: 1px solid #455574;
            border-radius: 7px;
            background: #0b1020;
            color: white;
        }
        button {
            margin-top: 22px;
            padding: 12px 18px;
            border: 0;
            border-radius: 7px;
            background: #1677c8;
            color: white;
            cursor: pointer;
        }
        a { color: #55c8ff; }
        .error { color: #ff8d8d; }
    </style>
</head>
<body>
<div class="container">
    <h1>Edit Athlete Profile</h1>

    <?php if ($error !== ''): ?>
        <p class="error">
            <?= htmlspecialchars($error, ENT_QUOTES, 'UTF-8') ?>
        </p>
    <?php endif; ?>

    <form method="post">
        <input
            type="hidden"
            name="csrf_token"
            value="<?= htmlspecialchars($_SESSION['csrf_token'], ENT_QUOTES, 'UTF-8') ?>"
        >

        <label for="name">Full name</label>
        <input
            id="name"
            name="name"
            type="text"
            maxlength="100"
            required
            value="<?= htmlspecialchars($user['name'], ENT_QUOTES, 'UTF-8') ?>"
        >

        <label for="email">Email address</label>
        <input
            id="email"
            name="email"
            type="email"
            maxlength="255"
            required
            value="<?= htmlspecialchars($user['email'], ENT_QUOTES, 'UTF-8') ?>"
        >

        <button type="submit">Save Changes</button>
    </form>

    <p><a href="profile_fetch.php">View My Profile</a></p>
    <p><a href="index.php">Back to Dashboard</a></p>
</div>
</body>
</html>
