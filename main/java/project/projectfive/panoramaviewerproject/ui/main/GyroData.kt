package project.projectfive.panoramaviewerproject.ui.main

class GyroData {
    var x:Float = 0.0f
    var y:Float = 0.0f
    var z:Float = 0.0f
    var iX:Float = 0.0f
    var iY:Float = 0.0f
    var iZ:Float = 0.0f

    fun updateData(x:Float, y:Float, z:Float, iX:Float, iY:Float, iZ:Float){
        this.x = x
        this.y = y
        this.z = z
        this.iX = iX
        this.iY = iY
        this.iZ = iZ
    }
}