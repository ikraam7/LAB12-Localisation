<?php
include_once __DIR__ . '/../contract/CrudContract.php';
include_once __DIR__ . '/../model/TrackPoint.php';
include_once __DIR__ . '/../db/Database.php';

class TrackPointRepository implements CrudContract {

    private $db;

    public function __construct() {
        $this->db = new Database();
    }

    // INSERT dans la table trackpoint
    public function insert($point) {
        $sql  = "INSERT INTO trackpoint(tp_lat, tp_lng, tp_time, tp_device) VALUES (?, ?, ?, ?)";
        $stmt = $this->db->getPdo()->prepare($sql);
        $stmt->execute([
            $point->getLat(),
            $point->getLng(),
            $point->getRecordedAt(),
            $point->getDeviceId(),
        ]);
        return true;
    }

    // SELECT tous les points — retourne tableau associatif
    public function findAll() {
        $stmt = $this->db->getPdo()->prepare("SELECT * FROM trackpoint ORDER BY tp_time DESC");
        $stmt->execute();
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    public function modify($obj)  {}
    public function remove($obj)  {}
    public function findById($id) {}
}