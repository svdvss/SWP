package com.example.androidstudioproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.retry_problem.*
import java.util.*

class RetryProblemScreen : AppCompatActivity() {
    private val DataListRetry = mutableListOf<Data>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.retry_problem)

        val db = FirebaseFirestore.getInstance()

        val user = intent.getStringExtra("user").toString()

        val docRef = db.collection("다시 풀기")
            .document(user)
            .collection(user)

        docRef
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document.get("base").toString() == "yes")
                        continue

                    //name info grade subject detailSubject
                    DataListRetry.add(
                        Data(
                            document.id,
                            document.get("문제 정보").toString(),
                            document.get("학년").toString(),
                            document.get("과목").toString(),
                            document.get("세부과목").toString()
                        )
                    )
                }

                val customAdapter = CustomAdapter(DataListRetry)

                retryProblemList.layoutManager = LinearLayoutManager(this)
                retryProblemList.adapter = customAdapter

                //RecyclerView에 onClickListener 붙이기
                customAdapter.setItemClickListener(object : CustomAdapter.OnItemClickListener {
                    override fun onClick(v: View, position: Int, DataList: List<Data>) {
                        val intent = Intent(v.context, RetryProblemSelectScreen::class.java)

                        intent.putExtra("user", user)
                        intent.putExtra("이름", DataList[position].name)
                        intent.putExtra("문제 정보", DataList[position].info)
                        intent.putExtra("학년", DataList[position].grade)
                        intent.putExtra("과목", DataList[position].subject)
                        intent.putExtra("세부과목", DataList[position].detailSubject)

                        startActivity(intent)
                    }
                })


                Collections.sort(DataListRetry, Comparator() { data1: Data, data2: Data ->
                    val answer1 =
                        data1.info.substring(data1.info.length - 3, data1.info.length - 1)
                            .trim().toInt()
                    val answer2 =
                        data2.info.substring(data2.info.length - 3, data2.info.length - 1)
                            .trim().toInt()

                    if(data1.subject == data2.subject)
                        answer1 - answer2
                    else
                        data1.subject.compareTo(data2.subject)
                })
            }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, BasicScreen::class.java))
    }

    fun home(v : View){
        startActivity(Intent(this, BasicScreen::class.java))
    }
}