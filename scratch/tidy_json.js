const fs = require('fs');
const path = require('path');

const targetDir = path.join('f:', 'Git', 'AshVehicle', 'src', 'main', 'resources', 'data', 'ashvehicle', 'sbw', 'vehicles');

const KEY_PRIORITY = [
    "ID", "Type", "MaxHealth", "MaxEnergy", "Mass", "Armored",
    "VehicleIcon", "ContainerIcon", "HudType",
    "OBB", "Seats", "Weapons",
    "EngineType", "EngineInfo", "EngineSound",
    "FlightInfo", "VtolInfo", "FlapInfo", "GearInfo",
    "TurretPos", "TurretTurnSpeed", "TurretPitchRange", "BarrelPos",
    "HasDecoy", "InertiaRotateRate", "RotateOffsetHeight",
    "DamageModifiers", "DestroyInfo", "ThirdPersonCameraPos",
    "CollisionLevel", "TerrainCompat"
];

function sortObject(obj) {
    if (Array.isArray(obj)) {
        return obj.map(sortObject);
    } else if (obj !== null && typeof obj === 'object') {
        const sorted = {};
        const keys = Object.keys(obj).sort((a, b) => {
            const idxA = KEY_PRIORITY.indexOf(a);
            const idxB = KEY_PRIORITY.indexOf(b);
            
            if (idxA !== -1 && idxB !== -1) return idxA - idxB;
            if (idxA !== -1) return -1;
            if (idxB !== -1) return 1;
            
            return a.localeCompare(b);
        });
        
        for (const key of keys) {
            sorted[key] = sortObject(obj[key]);
        }
        return sorted;
    }
    return obj;
}

const files = fs.readdirSync(targetDir);

for (const file of files) {
    if (file.endsWith('.json')) {
        const filePath = path.join(targetDir, file);
        console.log(`Tidying: ${file}`);
        try {
            const content = fs.readFileSync(filePath, 'utf8');
            const json = JSON.parse(content);
            const sortedJson = sortObject(json);
            fs.writeFileSync(filePath, JSON.stringify(sortedJson, null, 2), 'utf8');
        } catch (err) {
            console.error(`Error processing ${file}: ${err.message}`);
        }
    }
}
