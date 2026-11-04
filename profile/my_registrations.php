
<?php
require_once __DIR__ . '/../config/config.php';
requireLogin();

$userId = (int) $_SESSION['user_id'];

$stmt = $pdo->prepare(
    'SELECT
        r.id AS registration_id,
        r.status,
        r.registered_at,
        e.title,
        e.sport,
        e.event_date,
        e.venue,
        e.distance_km,
        e.entry_fee
     FROM registrations r
     INNER JOIN events e ON r.event_id = e.id
     WHERE r.user_id = ?
     ORDER BY r.registered_at DESC'
);

$stmt->execute([$userId]);
$registrations = $stmt->fetchAll();
?>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>My Registrations | Kinetra</title>

<style>
* {
    box-sizing: border-box;
}

body {
    margin: 0;
    background: #080D18;
    color: #F7F9FC;
    font-family: Arial, sans-serif;
}

header {
    background: #10182B;
    padding: 22px 6%;
    border-bottom: 1px solid #26334A;
}

.logo {
    font-size: 27px;
    font-weight: 900;
    letter-spacing: 4px;
}

.logo span {
    color: #52D9FF;
}

.container {
    max-width: 1000px;
    margin: 40px auto;
    padding: 20px;
}

.subtitle {
    color: #8492A8;
    line-height: 1.6;
}

.registration-card {
    background: #10182B;
    border: 1px solid #26334A;
    border-radius: 14px;
    padding: 24px;
    margin: 20px 0;
}

.registration-card h2 {
    color: #52D9FF;
    margin-top: 0;
}

.registration-card p {
    color: #B8C4D8;
    line-height: 1.7;
}

.status {
    display: inline-block;
    background: #123A2B;
    color: #A8FFD0;
    padding: 8px 13px;
    border-radius: 20px;
    font-size: 13px;
    text-transform: capitalize;
}

.status.cancelled {
    background: #4A202B;
    color: #FFDCE2;
}

.status.completed {
    background: #26334A;
    color: #B8C4D8;
}

.button {
    display: inline-block;
    background: #356BFF;
    color: white;
    padding: 12px 18px;
    border-radius: 8px;
    text-decoration: none;
    margin-top: 20px;
}

.empty {
    background: #10182B;
    padding: 25px;
    border-radius: 12px;
    color: #8492A8;
}
</style>
</head>

<body>

<header>
    <div class="logo">KINETRA<span>.</span></div>
    <p class="subtitle">Your Sports Journey</p>
</header>

<main class="container">

    <h1>My Registrations</h1>

    <p class="subtitle">
        View all the sports events you have registered for.
    </p>

    <?php if (count($registrations) > 0): ?>

        <?php foreach ($registrations as $registration): ?>

            <?php
            $status = $registration['status'];
            ?>

            <div class="registration-card">

                <h2><?= e($registration['title']) ?></h2>

                <p>
                    <strong>Sport:</strong>
                    <?= e($registration['sport']) ?>
                </p>

                <p>
                    <strong>Event Date:</strong>
                    <?= date('d M Y', strtotime($registration['event_date'])) ?>
                </p>

                <p>
                    <strong>Venue:</strong>
                    <?= e($registration['venue']) ?>
                </p>

                <?php if ($registration['distance_km'] !== null): ?>
                    <p>
                        <strong>Distance:</strong>
                        <?= e((string) $registration['distance_km']) ?> km
                    </p>
                <?php endif; ?>

                <p>
                    <strong>Entry Fee:</strong>
                    ₹<?= number_format((float) $registration['entry_fee'], 2) ?>
                </p>

                <p>
                    <strong>Registered On:</strong>
                    <?= date('d M Y', strtotime($registration['registered_at'])) ?>
                </p>

                <p>
                    <strong>Status:</strong>
                    <span class="status <?= e($status) ?>">
                        <?= e($status) ?>
                    </span>
                </p>

            </div>

        <?php endforeach; ?>

    <?php else: ?>

        <div class="empty">
            <h3>No registrations yet.</h3>
            <p>Explore sports events and register for your first event!</p>
        </div>

    <?php endif; ?>

    <a class="button" href="index.php">Back to Dashboard</a>
    <a class="button" href="events.php">Explore Events</a>

</main>

</body>
</html>
