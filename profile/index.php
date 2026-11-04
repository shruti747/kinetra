
<?php
require_once __DIR__ . '/../config/config.php';
requireLogin();

$userId = (int) $_SESSION['user_id'];
$name = $_SESSION['user_name'] ?? 'Kinetra User';

// Overall fitness statistics
$stmt = $pdo->prepare(
    'SELECT
        COUNT(*) AS total_activities,
        COALESCE(SUM(distance_km), 0) AS total_distance,
        COALESCE(SUM(duration_seconds), 0) AS total_duration
     FROM activities
     WHERE user_id = ?'
);
$stmt->execute([$userId]);
$stats = $stmt->fetch();

// Activity-wise statistics
$stmt = $pdo->prepare(
    'SELECT activity_type, COUNT(*) AS activity_count,
            COALESCE(SUM(distance_km), 0) AS total_distance
     FROM activities
     WHERE user_id = ?
     GROUP BY activity_type'
);
$stmt->execute([$userId]);
$typeStats = $stmt->fetchAll();

$maxTypeCount = 0;

foreach ($typeStats as $item) {
    $maxTypeCount = max(
        $maxTypeCount,
        (int) $item['activity_count']
    );
}

// Workout duration
$totalSeconds = (int) $stats['total_duration'];
$hours = intdiv($totalSeconds, 3600);
$minutes = intdiv($totalSeconds % 3600, 60);
$durationText = $hours . 'h ' . $minutes . 'm';
?>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Kinetra | Fitness Dashboard</title>

<style>
* {
    box-sizing: border-box;
}

body {
    margin: 0;
    font-family: Arial, sans-serif;
    background: #080D18;
    color: #F7F9FC;
}

header {
    background: #10182B;
    border-bottom: 1px solid #26334A;
    padding: 22px 6%;
}

.logo {
    font-size: 27px;
    font-weight: 900;
    letter-spacing: 4px;
}

.logo span {
    color: #52D9FF;
}

header p,
.welcome p,
.card p {
    color: #8492A8;
    line-height: 1.6;
}

main {
    max-width: 1100px;
    margin: 35px auto;
    padding: 20px;
}

.welcome {
    margin-bottom: 28px;
}

.welcome h2 {
    font-size: 28px;
}

.section-title {
    margin-top: 35px;
    margin-bottom: 18px;
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 18px;
}

.stat-card,
.card,
.activity-card {
    background: #10182B;
    border: 1px solid #26334A;
    border-radius: 14px;
    padding: 22px;
}

.stat-card h3 {
    color: #8492A8;
    font-size: 14px;
    font-weight: normal;
}

.stat-value {
    color: #52D9FF;
    font-size: 28px;
    font-weight: bold;
    margin: 12px 0 0;
}

.card-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 18px;
}

a.button {
    display: inline-block;
    background: #356BFF;
    color: white;
    text-decoration: none;
    padding: 12px 18px;
    border-radius: 8px;
    margin-top: 8px;
}

a.button:hover {
    background: #2455DA;
}

.type-row {
    margin: 22px 0;
}

.type-heading {
    display: flex;
    justify-content: space-between;
    gap: 12px;
    margin-bottom: 10px;
}

.type-heading span:last-child {
    color: #8492A8;
    font-size: 14px;
}

.bar-background {
    height: 10px;
    background: #26334A;
    border-radius: 20px;
    overflow: hidden;
}

.bar-fill {
    height: 100%;
    background: #52D9FF;
    border-radius: 20px;
}

.empty-message {
    color: #8492A8;
    line-height: 1.6;
}

footer {
    text-align: center;
    color: #8492A8;
    padding: 25px;
}

@media (max-width: 700px) {
    .stats-grid,
    .card-grid {
        grid-template-columns: 1fr;
    }

    .welcome h2 {
        font-size: 23px;
    }
}
</style>
</head>

<body>

<header>
    <div class="logo">KINETRA<span>.</span></div>
    <p>Sports & Fitness Management</p>
</header>

<main>

    <section class="welcome">
        <h2>Welcome, <?= e($name) ?>!</h2>
        <p>Your fitness journey, all in one place.</p>
    </section>

    <h2 class="section-title">Your Fitness Statistics</h2>

    <section class="stats-grid">

        <div class="stat-card">
            <h3>Total Distance</h3>
            <p class="stat-value">
                <?= number_format((float) $stats['total_distance'], 2) ?> km
            </p>
        </div>

        <div class="stat-card">
            <h3>Total Activities</h3>
            <p class="stat-value">
                <?= (int) $stats['total_activities'] ?>
            </p>
        </div>

        <div class="stat-card">
            <h3>Total Workout Time</h3>
            <p class="stat-value">
                <?= e($durationText) ?>
            </p>
        </div>

    </section>

    <h2 class="section-title">Activity Breakdown</h2>

    <section class="activity-card">

        <?php if (count($typeStats) > 0): ?>

            <?php foreach ($typeStats as $item): ?>

                <?php
                $count = (int) $item['activity_count'];

                $percentage = $maxTypeCount > 0
                    ? ($count / $maxTypeCount) * 100
                    : 0;
                ?>

                <div class="type-row">

                    <div class="type-heading">
                        <strong>
                            <?= e(ucfirst($item['activity_type'])) ?>
                        </strong>

                        <span>
                            <?= $count ?> activities ·
                            <?= number_format((float) $item['total_distance'], 2) ?> km
                        </span>
                    </div>

                    <div class="bar-background">
                        <div
                            class="bar-fill"
                            style="width: <?= $percentage ?>%;"
                        ></div>
                    </div>

                </div>

            <?php endforeach; ?>

        <?php else: ?>

            <p class="empty-message">
                No activities recorded yet. Add your first workout!
            </p>

        <?php endif; ?>

    </section>

    <h2 class="section-title">Explore Kinetra</h2>

    <section class="card-grid">

        <div class="card">
            <h3>Sports Events</h3>
            <p>Discover upcoming sports events and register to participate.</p>
            <a class="button" href="events.php">Explore Events</a>
        </div>

        <div class="card">
            <h3>My Registrations</h3>
            <p>View all sports events you have registered for.</p>
            <a class="button" href="my_registrations.php">
                View My Registrations
            </a>
        </div>

        <div class="card">
            <h3>Track Your Activities</h3>
            <p>Record running, walking, cycling and swimming activities.</p>
            <a class="button" href="add_activity.php">Add Activity</a>
        </div>

        <div class="card">
            <h3>Activity History</h3>
            <p>View your saved workouts and performance.</p>
            <a class="button" href="activity_history.php">View History</a>
        </div>

        <div class="card">
            <h3>Manage Your Profile</h3>
            <p>View and update your account details.</p>
            <a class="button" href="profile_edit.php">Edit Profile</a>
        </div>

    </section>

</main>

<footer>
    Kinetra &copy; <?= date('Y') ?> | Keep Moving. Keep Improving.
</footer>

</body>
</html>
