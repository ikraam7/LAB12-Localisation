<?php
// Retourne tous les points GPS en JSON
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    include_once __DIR__ . '/../repository/TrackPointRepository.php';
    sendAllPoints();
}

function sendAllPoints() {
    $repo = new TrackPointRepository();
    header('Content-Type: application/json; charset=utf-8');
    // Clé JSON "points" — lue par MapsActivity
    echo json_encode(['points' => $repo->findAll()]);
}