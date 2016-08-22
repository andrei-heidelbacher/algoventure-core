function doorSwitch(objectManager, obj, movedObj, direction) {
    var Utils = Packages.com.aheidelbacher.algostorm.engine.physics2d.PhysicsSystem.Companion;
    if (Utils.intersects(obj, movedObj)) {
        obj.gid = obj.get("openedGid");
    } else {
        Utils.transform(movedObj, -direction.dx, -direction.dy, 0);
        if (Utils.intersects(obj, movedObj)) {
            obj.gid = obj.get("closedGid");
        }
        Utils.transform(movedObj, direction.dx, direction.dy, 0);
    }
}
