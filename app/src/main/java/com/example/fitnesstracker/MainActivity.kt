package com.example.fitnesstracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers



class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var trainingAdapter: TrainingAdapter
    private val trainingList = mutableListOf<Training>()

    private lateinit var database: AppDatabase
    private lateinit var trainingDao: TrainingDao

    private val addTrainingLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val trainingName =
                data?.getStringExtra("trainingName") ?: return@registerForActivityResult
            val trainingCategory =
                data.getStringExtra("trainingCategory") ?: return@registerForActivityResult
            val calories = data.getIntExtra("calories", 0)
            val date = data.getStringExtra("date") ?: return@registerForActivityResult

            val newTraining = Training(
                name = trainingName,
                category = trainingCategory,
                calories = calories,
                date = date
            )

            // Додати нове тренування до бази даних
            lifecycleScope.launch {
                val id = trainingDao.insert(newTraining)
                val trainingWithId = newTraining.copy(id = id)
                trainingList.add(trainingWithId)
                trainingAdapter.notifyItemInserted(trainingList.size - 1)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ініціалізація бази даних
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "fitness_tracker_db"
        ).build()
        trainingDao = database.trainingDao()

        // Ініціалізація RecyclerView та адаптера
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        trainingAdapter = TrainingAdapter(trainingList)
        recyclerView.adapter = trainingAdapter

        // Завантаження даних із бази
        loadTrainings()

        // Кнопка додавання тренування
        val addButton: Button = findViewById(R.id.addTrainingButton)
        addButton.setOnClickListener {
            val intent = Intent(this, AddTrainingActivity::class.java)
            addTrainingLauncher.launch(intent)
        }

        // Кнопка перегляду статистики
        val viewStatisticsButton: Button = findViewById(R.id.buttonViewStatistics)
        viewStatisticsButton.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadTrainings() {
        lifecycleScope.launch {
            // Використовуємо collect для підписки на Flow і отримуємо перший елемент
            trainingDao.getAllTrainings().collect { trainings ->
                val firstTraining =
                    trainings.firstOrNull() // Беремо перший елемент або null, якщо список порожній
                if (firstTraining != null) {
                    trainingList.add(firstTraining)
                    trainingAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}


