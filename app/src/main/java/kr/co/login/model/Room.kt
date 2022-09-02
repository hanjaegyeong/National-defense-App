package kr.co.login.model

class Room {
    var id: String = ""
    var title: String = ""
    var users: String = ""
    var write: String = ""
    var timestamp: Long = 0

    constructor()

    constructor(title:String, creatorName: String, write:String){
        this.title = title
        this.write = write
        this.timestamp = System.currentTimeMillis()
        users = creatorName
    }
}