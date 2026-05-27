<?php
// Modèle représentant une ligne de la table trackpoint
class TrackPoint {
    private $id;
    private $lat;
    private $lng;
    private $recordedAt;
    private $deviceId;

    public function __construct($id, $lat, $lng, $recordedAt, $deviceId) {
        $this->id         = $id;
        $this->lat        = $lat;
        $this->lng        = $lng;
        $this->recordedAt = $recordedAt;
        $this->deviceId   = $deviceId;
    }

    public function getId()         { return $this->id; }
    public function getLat()        { return $this->lat; }
    public function getLng()        { return $this->lng; }
    public function getRecordedAt() { return $this->recordedAt; }
    public function getDeviceId()   { return $this->deviceId; }

    public function setId($id)                 { $this->id = $id; }
    public function setLat($lat)               { $this->lat = $lat; }
    public function setLng($lng)               { $this->lng = $lng; }
    public function setRecordedAt($recordedAt) { $this->recordedAt = $recordedAt; }
    public function setDeviceId($deviceId)     { $this->deviceId = $deviceId; }
}