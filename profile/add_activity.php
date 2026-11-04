
<?php
require_once __DIR__ . '/../config/config.php';
requireLogin();

$error = '';
$success = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    verifyCsrf();

    $activityType = trim($_POST['activity_type'] ?? 'running');
    $distance = filter_var($_POST['distance_km'] ?? '', FILTER_VALIDATE_FLOAT);
    $duration = filter_var($_POST['duration_seconds'] ?? '', FILTER_VALIDATE_INT);

    $allowedTypes = ['running', 'walking', 'cycling', 'swimming'];

    if (!in_array($activityType, $allowedTypes, true)) {
        $error = 'Please select a valid activity type.';
    } elseif ($distance === false || $distance <= 0) {
        $error = 'Enter a distance greater than zero.';
    } elseif ($duration === false || $duration <= 0) {
        $error = 'Enter a duration greater than zero.';
    } else {
        $totalSeconds = (int) $duration;
        $paceSeconds = (int) round($totalSeconds / $distance);
        $pace = floor($paceSeconds / 60) . ':' .
            str_pad((string) ($paceSeconds % 60), 2, '0', STR_PAD_LEFT);

        $stmt = $pdo->prepare(
            "INSERT INTO activities
            (user_id, activity_type, distance_km, duration_seconds,
             average_pace, source)
            VALUES (?, ?, ?, ?, ?, 'gps_estimate')"
        );

        $stmt->execute([
            $_SESSION['user_id'],
            $activityType,
            $distance,
            $totalSeconds,
            $pace
        ]);

        $success = 'Activity saved successfully.';
    }
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Add Activity | Kinetra</title>
</head>
<body>
    <h1>Add an Activity</h1>

    <?php if ($error): ?>
        <p style="color:red;"><?= e($error) ?></p>
    <?php endif; ?>

    <?php if ($success): ?>
        <p style="color:green;"><?= e($success) ?></p>
        <p><a href="index.php">Return to dashboard</a></p>
    <?php endif; ?>

    <form method="POST">
        <input type="hidden" name="csrf_token"
               value="<?= e($_SESSION['csrf_token']) ?>">

        <p>
            <label>Activity type</label><br>
            <select name="activity_type" required>
                <option value="running">Running</option>
                <option value="walking">Walking</option>
                <option value="cycling">Cycling</option>
                <option value="swimming">Swimming</option>
            </select>
        </p>

        <p>
            <label>Distance (km)</label><br>
            <input type="number" name="distance_km"
                   min="0.01" step="0.01" required>
        </p>

        <p>
            <label>Duration (seconds)</label><br>
            <input type="number" name="duration_seconds"
                   min="1" required>
        </p>

        <button type="submit">Save Activity</button>
    </form>

    <p><a href="index.php">Back to dashboard</a></p>
</body>
</html>
