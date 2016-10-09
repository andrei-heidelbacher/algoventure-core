function getDrunkWalkInput(objectManager, objectId) {
    var v = Math.random();
    var directions = Direction.values();
    for (i = 0; i < directions.length; i++) {
        if (v < (i + 1) / directions.length) {
            return new Action.Move(objectId, directions[i]);
        }
    }
    return new Action.Wait(objectId);
};
