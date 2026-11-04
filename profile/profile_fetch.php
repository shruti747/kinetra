
<?php
declare(strict_types=1);

require_once __DIR__ . '/../config/config.php';
requireLogin();

$userId = (int) $_SESSION['user_id'];

$stmt = $pdo->prepare(
    'SELECT id, name, email, created_at
     FROM users
     WHERE id = ?'
);
$stmt->execute([$userId]);
$user = $stmt->fetch();

if (!$user) {
    http_response_code(404);
    exit('Athlete profile not found.');
}

function profileEscape(?string $value): string {
    return htmlspecialchars($value ?? '', ENT_QUOTES, 'UTF-8');
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Kinetra | My Profile</title>
    <style>
        body {
            margin: 0;
            padding: 30px 16px;
            font-family: Arial, sans-serif;
            background: #0b1020;
            color: #f4f7ff;
        }
        .container {
            max-width: 600px;
            margin: 30px auto;
            padding: 28px;
            background: #141f35;
            border: 1px solid #293956;
            border-radius: 14px;
        }
        h1 { color: #55c8ff; }
        .detail {
            padding: 14px 0;
            border-bottom: 1px solid #293956;
            overflow-wrap: anywhere;
        }
        .label { color: #a9b8d1; }
        .actions { display: flex; gap: 18px; flex-wrap: wrap; margin-top: 24px; }
        a { color: #55c8ff; }
        .success { color: #7ce5b2; }
    </style>
</head>
<body>
<div class="container">
    <h1>My Athlete Profile</h1>

    <?php if (isset($_GET['updated'])): ?>
        <p class="success">Your profile was updated successfully.</p>
    <?php endif; ?>

    <div class="detail">
        <div class="label">Athlete ID</div>
        <?= (int) $user['id'] ?>
    </div>

    <div class="detail">
        <div class="label">Full name</div>
        <?= profileEscape($user['name']) ?>
    </div>

    <div class="detail">
        <div class="label">Email address</div>
        <?= profileEscape($user['email']) ?>
    </div>

    <div class="detail">
        <div class="label">Member since</div>
        <?= profileEscape($user['created_at']) ?>
    </div>

    <div class="actions">
        <a href="profile_edit.php">Edit Profile</a>
        <a href="index.php">Dashboard</a>
    </div>
</div>
</body>
</html>
