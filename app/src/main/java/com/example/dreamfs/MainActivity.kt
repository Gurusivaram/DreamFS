package com.example.dreamfs

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.dreamfilesystem.Constants.FILE_TYPE
import com.example.dreamfilesystem.Constants.FOLDER_TYPE
import com.example.dreamfilesystem.DreamFS

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dreamFS = DreamFS(this)
        
        val btn = findViewById<Button>(R.id.button)
        val edt = findViewById<EditText>(R.id.edt)

        btn.setOnClickListener {
            println(dreamFS.create("/in", FOLDER_TYPE))
            println(dreamFS.create("/de", FOLDER_TYPE))
            println(dreamFS.create("/fr", FOLDER_TYPE))
            println(dreamFS.create("/us", FOLDER_TYPE))
            println(dreamFS.create("/us/ca", FOLDER_TYPE))
            println(dreamFS.create("/us/ny", FOLDER_TYPE))
            println(dreamFS.create("/us/tx", FOLDER_TYPE))
            println(dreamFS.create("/in/dl", FOLDER_TYPE))
            println(dreamFS.create("/in/ka", FOLDER_TYPE))
            println(dreamFS.create("/in/mh", FOLDER_TYPE))
            println(dreamFS.create("/in/ka/areas", FOLDER_TYPE))
            println(dreamFS.create("/in/ka/malls", FOLDER_TYPE))
            println(dreamFS.create("/in/ka/areas/hsr", FOLDER_TYPE))
            println(dreamFS.create("/in/ka/areas/hsr/pubs", FOLDER_TYPE))
            println(dreamFS.create("/in/ka/areas/indira-nagar", FOLDER_TYPE))
            println(dreamFS.create("/in/ka/malls/orion.png", FILE_TYPE))
            println(dreamFS.create("/in/ka/malls/phoenix.png", FILE_TYPE))
            println(dreamFS.create("/in/ka/capital.txt", FILE_TYPE))
            println(dreamFS.create("/in/ka/areas/hsr/pubs/hammered.png", FILE_TYPE))
            println(dreamFS.create("/in/ka/areas/hsr/pubs/plan-b.png", FILE_TYPE))
            println(dreamFS.create("/us/ca/google", FOLDER_TYPE))
            println(dreamFS.create("/us/ca/google/address.txt", FILE_TYPE))
            println(dreamFS.create("/us/ny/central-park.txt", FILE_TYPE))

            println(dreamFS.scan("/in"))

//            println(dreamFS.create("/in/kl", FOLDER_TYPE))

//            println(dreamFS.create("/IN/MH/photo.png", FILE_TYPE))

//            println(dreamFS.read("/IN/KA/capital.txt"))
//            println(dreamFS.write("/IN/KA/capital.txt", "sample test"))
//            println(dreamFS.read("/IN/KA/capital.txt"))

//            println(dreamFS.move("/US/CA/google/address.txt", "/fr"))
//            println(dreamFS.move("/in/mh/AREAS", "/in/mh/AREAS/hsr"))

//            println(dreamFS.rename("/IN/MH/KA/MALLS/phoenix.png", "pho.png"))

//            println(dreamFS.delete("/IN/MH/KA"))

//            println(dreamFS.cTime("/IN/MH/AREAS/HSR/pubs/HAMMERED.png"))
//            println(dreamFS.mTime("/IN/MH/AREAS/HSR/pubs/HAMMERED.png"))

            edt.text.clear()
        }
    }
}