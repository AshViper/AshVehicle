$priority = @(
    "ID", "Type", "MaxHealth", "MaxEnergy", "Mass", "Armored",
    "VehicleIcon", "ContainerIcon", "HudType",
    "OBB", "Seats", "Weapons",
    "EngineType", "EngineInfo", "EngineSound",
    "FlightInfo", "VtolInfo", "FlapInfo", "GearInfo",
    "TurretPos", "TurretTurnSpeed", "TurretPitchRange", "BarrelPos",
    "HasDecoy", "InertiaRotateRate", "RotateOffsetHeight",
    "DamageModifiers", "DestroyInfo", "ThirdPersonCameraPos",
    "CollisionLevel", "TerrainCompat"
)

function Sort-JsonObj($obj) {
    if ($obj -is [PSCustomObject]) {
        $sorted = [ordered]@{}
        $keys = $obj.PSObject.Properties.Name
        
        # Sort keys based on priority
        $sortedKeys = $keys | Sort-Object {
            $idx = [array]::IndexOf($priority, $_)
            if ($idx -eq -1) { $priority.Count } else { $idx }
        }, { $_ }
        
        foreach ($key in $sortedKeys) {
            $sorted[$key] = Sort-JsonObj $obj.$key
        }
        return [PSCustomObject]$sorted
    }
    elseif ($obj -is [array]) {
        return $obj | ForEach-Object { Sort-JsonObj $_ }
    }
    else {
        return $obj
    }
}

$dir = "f:\Git\AshVehicle\src\main\resources\data\ashvehicle\sbw\vehicles"
$files = Get-ChildItem -Path $dir -Filter "*.json"

foreach ($file in $files) {
    Write-Host "Tidying: $($file.Name)"
    $json = Get-Content -Path $file.FullName -Raw | ConvertFrom-Json
    $sortedJson = Sort-JsonObj $json
    $sortedJson | ConvertTo-Json -Depth 100 | Out-File -FilePath $file.FullName -Encoding utf8
}
