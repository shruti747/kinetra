
<?php
require_once __DIR__ . '/../config/config.php';
requireLogin();

$userId = (int) $_SESSION['user_id'];
$message = '';
$messageType = 'success';

// Handle event registration
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    verifyCsrf();

    $eventId = filter_input(INPUT_POST, 'event_id', FILTER_VALIDATE_INT);

    if (!$eventId || $eventId < 1) {
        $message = 'Invalid event selection.';
        $messageType = 'error';
    } else {
        $check = $pdo->prepare('SELECT id FROM events WHERE id = ?');
        $check->execute([$eventId]);

        if (!$check->fetch()) {
            $message = 'This event does not exist.';
            $messageType = 'error';
        } else {
            try {
                $stmt = $pdo->prepare(
                    "INSERT INTO registrations (user_id, event_id)
                     VALUES (?, ?)"
                );
                $stmt->execute([$userId, $eventId]);

                $message = 'You have successfully registered for the event!';
            } catch (PDOException $e) {
                if ($e->getCode() === '23000') {
                    $message = 'You have already registered for this event.';
                } else {
                    error_log($e->getMessage());
                    $message = 'Registration failed. Please try again.';
                    $messageType = 'error';
                }
            }
        }
    }
}

// Fetch events and the current user's registration status
$stmt = $pdo->prepare(
    'SELECT
        e.id,
        e.title,
        e.sport,
        e.description,
        e.event_date,
        e.venue,
        e.distance_km,
        e.entry_fee,
        r.status AS registration_status
     FROM events e
     LEFT JOIN registrations r
        ON r.event_id = e.id AND r.user_id = ?
     ORDER BY e.event_date ASC'
);
$stmt->execute([$userId]);
$events = $stmt->fetchAll();
?>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Sports Events | Kinetra</title>

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
    max-width: 1050px;
    margin: 40px auto;
    padding: 20px;
}

.subtitle {
    color: #8492A8;
    line-height: 1.6;
}

.event-card {
    background: #10182B;
    border: 1px solid #26334A;
    border-radius: 14px;
    padding: 24px;
    margin: 20px 0;
}

.event-card h2 {
    color: #52D9FF;
    margin-top: 0;
}

.event-card p {
    color: #B8C4D8;
    line-height: 1.7;
}

button {
    padding: 12px 20px;
    border: 0;
    border-radius: 8px;
    background: #356BFF;
    color: white;
    font-size: 15px;
    cursor: pointer;
}

button:disabled {
    background: #38516C;
    cursor: not-allowed;
}

.notice {
    padding: 14px;
    border-radius: 8px;
    margin: 20px 0;
    background: #123A2B;
    color: #A8FFD0;
}

.notice.error {
    background: #4A202B;
    color: #FFDCE2;
}

.empty {
    background: #10182B;
    padding: 24px;
    border-radius: 12px;
    color: #8492A8;
}

a.back {
    display: inline-block;
    margin-top: 20px;
    color: #52D9FF;
    text-decoration: none;
}
</style>
</head>

<body>

<header>
    <div class="logo">KINETRA<span>.</span></div>
    <p class="subtitle">Discover. Participate. Achieve.</p>
</header>

<main class="container">

    <h1>Sports Events</h1>
    <p class="subtitle">
        Discover upcoming sports events and register to participate.
    </p>

    <?php if ($message): ?>
        <div class="notice <?= $messageType === 'error' ? 'error' : '' ?>">
            <?= e($message) ?>
        </div>
    <?php endif; ?>

    <?php if (count($events) > 0): ?>

        <?php foreach ($events as $event): ?>

            <div class="event-card">

                <h2><?= e($event['title']) ?></h2>

                <p>
                    <strong>Sport:</strong>
                    <?= e($event['sport']) ?>
                </p>

                <p>
                    <strong>Description:</strong>
                    <?= e($event['description'] ?? 'No description available.') ?>
                </p>

                <p>
                    <strong>Date:</strong>
                    <?= date('d M Y', strtotime($event['event_date'])) ?>
                </p>

                <p>
                    <strong>Venue:</strong>
                    <?= e($event['venue']) ?>
                </p>

                <?php if ($event['distance_km'] !== null): ?>
                    <p>
                        <strong>Distance:</strong>
                        <?= e((string) $event['distance_km']) ?> km
                    </p>
                <?php endif; ?>

                <p>
                    <strong>Entry Fee:</strong>
                    ₹<?= number_format((float) $event['entry_fee'], 2) ?>
                </p>

                <?php if ($event['registration_status'] === 'registered'): ?>

                    <button disabled>Already Registered</button>

                <?php elseif (strtotime($event['event_date']) < strtotime(date('Y-m-d'))): ?>

                    <button disabled>Event Completed</button>

                <?php else: ?>

                    <form method="POST" action="events.php">
                        <input
                            type="hidden"
                            name="csrf_token"
                            value="<?= e($_SESSION['csrf_token']) ?>"
                        >

                        <input
                            type="hidden"
                            name="event_id"
                            value="<?= (int) $event['id'] ?>"
                        >

                        <button type="submit">Register Now</button>
                    </form>

                <?php endif; ?>

            </div>

        <?php endforeach; ?>

    <?php else: ?>

        <div class="empty">
            <h3>No sports events available yet.</h3>
            <p>Please check again when new events are added.</p>
        </div>

    <?php endif; ?>

    <a class="back" href="index.php">← Back to Dashboard</a>

</main>

</body>
</html>
