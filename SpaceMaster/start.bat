@ECHO OFF

Set CP=%PRTI1516E_HOME%/lib/prticore.jar;./SpaceMaster.jar;

java -ea:se.pitch.spacemaster... -cp "%CP%" se.pitch.spacemaster.Main %1 %2 %3

pause