#!/bin/bash
# compile.sh - Compilation du projet Médiathèque

echo "=== Compilation du projet ArchiLog Médiathèque ==="

# Créer le répertoire de sortie
mkdir -p out

# Compiler tous les fichiers Java
javac -d out -sourcepath src \
  src/exceptions/*.java \
  src/modele/*.java \
  src/serveur/*.java \
  src/client/*.java

if [ $? -eq 0 ]; then
  echo "Compilation réussie ! Fichiers .class dans le répertoire 'out/'"
  echo ""
  echo "Pour lancer le serveur :"
  echo "  java -cp out serveur.AppServeur"
  echo ""
  echo "Pour lancer les clients :"
  echo "  java -cp out client.ClientReservation [hote] [port]"
  echo "  java -cp out client.ClientEmprunt     [hote] [port]"
  echo "  java -cp out client.ClientRetour      [hote] [port]"
else
  echo "Erreur de compilation."
  exit 1
fi
