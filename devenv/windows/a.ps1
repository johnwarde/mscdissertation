
$OldLocation = Get-Location
#$aryTargets = @('webapp', 'processor')
$aryTargets = @('webapp')
#$aryTargets = @('processor')
$strRoot = 'C:\jbdissertation\scm\mscdissertation\workspace'
$aryTargets | ForEach-Object {
    cd $strRoot\$_
    mvn package '-Dmaven.test.skip=true'
    $JarFilename = (Get-ChildItem $strRoot\$_\target\$_*.jar).FullName
    Write-Host ('Starting {0} copy to AWS S3 ({1})' -f $_,$JarFilename)
    aws s3 cp $JarFilename s3://johnwarde.net/
    Write-Host ('Finished {0} copy to AWS S3' -f $_)
}
