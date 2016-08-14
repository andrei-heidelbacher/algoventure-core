function getPlayerInput(objectManager, actorId) {
    var player = objectManager.get(actorId);
    var lastInput = player.get("input");
    player.remove("input");
    return lastInput;
}
