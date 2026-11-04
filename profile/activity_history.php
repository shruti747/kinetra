
<?php
require_once __DIR__ . '/../config/config.php';
requireLogin();

$userId = (int) $_SESSION['user_id'];

$stmt = $pdo->prepare(
    'SELECT activity_type, distance_km, duration_seconds,
            average_pace, source
     FROM activities
     WHERE user_id = ?'
);
$stmt->execute([$userId]);
$activities = $stmt->fetchAll();

function formatDuration(int $seconds): string {
    $minutes = intdiv($seconds, 60);
    $remainingSeconds = $seconds % 60;

    return $minutes . ' min ' . $remainingSeconds . ' sec';
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Activity History | Kinetra</title>

<style>
body {
    margin: 0;
    padding: 30px;
    background: #080D18;
    color: #F7F9FC;
    font-family: Arial, sans-serif;
}

.container {
    max-width: 900px;
    margin: auto;
}

h1 {
    color: #52D9FF;
}

.activity {
    background: #10182B;
    border: 1px solid #26334A;
    border-radius: 12px;
    padding: 20px;
    margin: 15px 0;
}

.activity p {
    color: #B8C4D8;
    line-height: 1.6;
}

a {
    display: inline-block;
    margin-top: 20px;
    padding: 12px 18px;
    background: #356BFF;
    color: white;
    text-decoration: none;
    border-radius: 8px;
}

.empty {
    color: #B8C4D8;
}
</style>
</head>

<body>

<div class="container">

<h1>Your Activity History</h1>
<p>Review your recorded fitness activities.</p>

<?php if (count($activities) > 0): ?>

    <?php foreach ($activities as $activity): ?>

        <div class="activity">

            <h2>
                <?= e(ucfirst($activity['activity_type'])) ?>
            </h2>

            <p>
                <strong>Distance:</strong>
                <?= number_format((float) $activity['distance_km'], 2) ?> km
            </p>

            <p>
                <strong>Duration:</strong>
                <?= formatDuration((int) $activity['duration_seconds']) ?>
            </p>

            <p>
                <strong>Average pace:</strong>
                <?= e((string) ($activity['average_pace'] ?? 'Not available')) ?>
            </p>

            <p>
                <strong>Source:</strong>
                <?= e((string) ($activity['source'] ?? 'Manual')) ?>
            </p>

        </div>

    <?php endforeach; ?>

<?php else: ?>

    <p class="empty">You have not recorded any activities yet.</p>

<?php endif; ?>

<a href="index.php">Back to Dashboard</a>

</div>

</body>
</html>
