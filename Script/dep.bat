@echo off
setlocal enabledelayedexpansion

:: Déclaration des variables
set "work_dir=E:\TSITO\COURS\s3\Git\sprit"
set "src=%work_dir%\Framework"
set "lib=%work_dir%\lib"

:: Créer une liste de tous les fichiers .java dans le répertoire src et ses sous-répertoires
dir /s /B "%src%\*.java" > sources.txt
dir /s /B "%lib%\*.jar" > libs.txt

:: Exécuter la commande javac
set "classpath="
for /F "delims=" %%i in (libs.txt) do set "classpath=!classpath!%%i;"
:: Exécuter la commande javac
javac -d "%src%" -cp "%classpath%" @sources.txt
:: Supprimer les fichiers sources.txt et libs.txt après la compilation
del sources.txt
del libs.txt
cd "%src%"
jar cvf "FrontContr.jar" *
echo Déploiement terminé.
