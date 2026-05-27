<?php
header('Content-Type: application/json; charset=utf-8');

// Accepte uniquement POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['ok' => false, 'msg' => 'POST uniquement']);
    exit;
}

include_once __DIR__ . '/../repository/TrackPointRepository.php';
include_once __DIR__ . '/../model/TrackPoint.php';

// Paramètres envoyés par Android
$lat    = $_POST['lat']    ?? null;
$lng    = $_POST['lng']    ?? null;
$ts     = $_POST['ts']     ?? null;
$device = $_POST['device'] ?? null;
$ip     = $_SERVER['REMOTE_ADDR'];

// Vérification des paramètres
if ($lat === null || $lng === null || $ts === null || $device === null) {
    http_response_code(400);
    echo json_encode(['ok' => false, 'msg' => 'Paramètres manquants', 'ip' => $ip]);
    exit;
}

try {
    $repo  = new TrackPointRepository();
    $point = new TrackPoint(null, $lat, $lng, $ts, $device);
    $repo->insert($point);
    echo json_encode(['ok' => true, 'ip' => $ip]);
} catch (Exception $ex) {
    http_response_code(500);
    echo json_encode(['ok' => false, 'msg' => $ex->getMessage(), 'ip' => $ip]);
}