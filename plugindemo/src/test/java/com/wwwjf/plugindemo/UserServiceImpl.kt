package com.wwwjf.plugindemo

class UserServiceImpl:UserService {
    override fun register(name:String) {
        println("register $name log......")
    }
}