function playerInput(objectManager, actorId) {
    return objectManager.get(actorId).properties.get("lastInput");
}
