
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
    exit('User not found.');
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    verifyCsrf();

    $name = trim($_POST['name'] ?? '');
    $email = trim($_POST['email'] ?? '');

    if ($name === '' || !filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $error = 'Enter a valid name and email address.';
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
                $error = 'That email address is already in use.';
            } else {
                error_log($e->getMessage());
                $error = 'Unable to save your profile.';
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
    <title>Kinetra - Profile Details</title>
</head>
<body>
    <h1>My Kinetra Profile</h1>

    <?php if ($error !== ''): ?>
        <p style="color:red;">
            <?= htmlspecialchars($error, ENT_QUOTES, 'UTF-8') ?>
        </p>
    <?php endif; ?>

    <form method="post">
        <input type="hidden" name="csrf_token"
            value="<?= htmlspecialchars($_SESSION['csrf_token'], ENT_QUOTES, 'UTF-8') ?>">

        <label for="name">Full name</label><br>
        <input id="name" name="name" maxlength="100" required
            value="<?= htmlspecialchars($user['name'], ENT_QUOTES, 'UTF-8') ?>"><br><br>

        <label for="email">Email address</label><br>
        <input id="email" name="email" type="email" maxlength="255" required
            value="<?= htmlspecialchars($user['email'], ENT_QUOTES, 'UTF-8') ?>"><br><br>

        <button type="submit">Save Profile</button>
    </form>

    <p><a href="index.php">Back to Dashboard</a></p>
</body>
</html>
