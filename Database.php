<?php
// Connexion PDO vers la base "localisation"
class Database {
    private $pdo;

    public function __construct() {
        $host   = 'localhost';
        $dbname = 'localisation';   // ← nom exact de ta base
        $user   = 'root';
        $pass   = '';

        try {
            $dsn       = "mysql:host=$host;dbname=$dbname;charset=utf8";
            $this->pdo = new PDO($dsn, $user, $pass, [
                PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            ]);
        } catch (Exception $ex) {
            die(json_encode(['ok' => false, 'error' => $ex->getMessage()]));
        }
    }

    public function getPdo() {
        return $this->pdo;
    }
}