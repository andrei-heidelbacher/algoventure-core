function doorSwitch(objectManager, obj, movedObj, direction) {
    if (PhysicsUtil.intersects(obj, movedObj)) {
        obj.gid = obj.getInt("openedGid");
    } else {
        PhysicsUtil.transform(movedObj, -direction.dx, -direction.dy);
        if (PhysicsUtil.intersects(obj, movedObj)) {
            obj.gid = obj.getInt("closedGid");
        }
        PhysicsUtil.transform(movedObj, direction.dx, direction.dy);
    }
}
