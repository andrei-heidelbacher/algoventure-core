function getPlayerInput(objectManager, actorId) {
    var player = objectManager.get(actorId);
    var lastInput = player.get("lastInput");
    player.remove("lastInput");
    return lastInput;
}
