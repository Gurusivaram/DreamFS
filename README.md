DreamFS
===============

DreamFS is a simple imaginary file system using which you can add/edit/remove the files or folders which are entirely stored in SQLite DB


Usage
------
```
...
import com.example.dreamfilesystem.DreamFS
...

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val dreamFS = DreamFS(this)
        dreamFS.create("/in", FOLDER_TYPE)
    }
}
```
