package com.example.unsripay

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class suliet : AppCompatActivity() {

    private lateinit var nimInput: EditText
    private lateinit var btnLanjut: Button
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.suliet)

        nimInput = findViewById(R.id.nim_input)
        btnLanjut = findViewById(R.id.btn_lanjut)

        // Fungsi untuk tombol kembali
        val backButton = findViewById<Button>(R.id.backbutton)
        backButton.setOnClickListener {
            finish() // Menutup activity ini
        }

        btnLanjut.setOnClickListener {
            val nim = nimInput.text.toString()
            if (nim.isEmpty()) {
                Toast.makeText(this, "Masukkan NIM terlebih dahulu", Toast.LENGTH_SHORT).show()
            } else {
                fetchDataAndNavigate(nim)
            }
        }
    }

    private fun fetchDataAndNavigate(nim: String) {
        // Akses database Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("unsripay/suliet")

        // Cari data berdasarkan NIM
        databaseReference.orderByChild("nim").equalTo(nim).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val sulietData = data.getValue(SulietData::class.java)
                        if (sulietData != null) {
                            // Kirim data ke halaman berikutnya
                            val intent = Intent(this@suliet, confirmsuliet::class.java)
                            intent.putExtra("nim", sulietData.nim)
                            intent.putExtra("nama", sulietData.nama)
                            intent.putExtra("tanggal", sulietData.tanggal)
                            intent.putExtra("tempat", sulietData.tempat)
                            intent.putExtra("nominal", sulietData.nominal)
                            intent.putExtra("selectedBank", "SULIET")
                            startActivity(intent)
                        }
                    }
                } else {
                    Toast.makeText(this@suliet, "Data tidak ditemukan untuk NIM: $nim", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@suliet, "Gagal mengambil data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

// Model Data SULIET
data class SulietData(
    val nim: String = "",
    val nama: String = "",
    val tanggal: String = "",
    val tempat: String = "",
    val nominal: Int = 0
)
