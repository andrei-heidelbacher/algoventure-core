function playerInput(objectManager, actorId) {
    var player = objectManager.get(actorId)
    var lastInput = player.get("lastInput");
    player.properties.remove("lastInput");
    return lastInput;
}
