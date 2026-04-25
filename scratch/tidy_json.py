import json
import os
from collections import OrderedDict

# Define key priority for sorting
KEY_PRIORITY = [
    "ID", "Type", "MaxHealth", "MaxEnergy", "Mass", "Armored",
    "VehicleIcon", "ContainerIcon", "HudType",
    "OBB", "Seats", "Weapons",
    "EngineType", "EngineInfo", "EngineSound",
    "FlightInfo", "VtolInfo", "FlapInfo", "GearInfo",
    "TurretPos", "TurretTurnSpeed", "TurretPitchRange", "BarrelPos",
    "HasDecoy", "InertiaRotateRate", "RotateOffsetHeight",
    "DamageModifiers", "DestroyInfo", "ThirdPersonCameraPos",
    "CollisionLevel", "TerrainCompat"
]

def sort_dict(d):
    if not isinstance(d, dict):
        return d
    
    # Sort keys based on priority, then alphabetically for unknown keys
    sorted_keys = sorted(d.keys(), key=lambda k: (KEY_PRIORITY.index(k) if k in KEY_PRIORITY else len(KEY_PRIORITY), k))
    
    res = OrderedDict()
    for k in sorted_keys:
        val = d[k]
        if isinstance(val, dict):
            res[k] = sort_dict(val)
        elif isinstance(val, list):
            res[k] = [sort_dict(i) if isinstance(i, dict) else i for i in val]
        else:
            res[k] = val
    return res

def tidy_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        try:
            data = json.load(f)
        except Exception as e:
            print(f"Error loading {filepath}: {e}")
            return

    # Sort the data
    ordered_data = sort_dict(data)
    
    # Write back with clean formatting
    with open(filepath, 'w', encoding='utf-8') as f:
        json.dump(ordered_data, f, indent=2, ensure_ascii=False)
    print(f"Tidied: {os.path.basename(filepath)}")

def main():
    target_dir = r"f:\Git\AshVehicle\src\main\resources\data\ashvehicle\sbw\vehicles"
    for filename in os.listdir(target_dir):
        if filename.endswith(".json"):
            tidy_file(os.path.join(target_dir, filename))

if __name__ == "__main__":
    main()
