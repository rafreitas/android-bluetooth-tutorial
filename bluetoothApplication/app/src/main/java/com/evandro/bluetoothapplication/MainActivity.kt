package com.evandro.bluetoothapplication

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var m_bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var m_pairedDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLED_BLUETOOTH = 1

    companion object {
        val EXTRA_ADRESS: String = "Device_Address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        //Verifica se o dispositivo suporta o Bluetooth
        if(m_bluetoothAdapter == null){
            Toast.makeText(applicationContext, "Bluetooth não suportado", Toast.LENGTH_SHORT)
                .show()
            true
        }
        //Solicita ativação do Bluetooth
        if (!m_bluetoothAdapter!!.isEnabled){
            val enabledBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enabledBluetoothIntent, REQUEST_ENABLED_BLUETOOTH)
        }
        btnRefresh.setOnClickListener{pairedDeviceList()}
    }

    private fun pairedDeviceList(){
        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices
        val list: ArrayList<BluetoothDevice> = ArrayList()

        if(!m_pairedDevices.isEmpty()){
            for (device: BluetoothDevice in m_pairedDevices){
                list.add(device)
                Log.i("device", ""+device)
            }
        } else {
            Toast.makeText(applicationContext, "Nenhum dispositivo pareado encontrado", Toast.LENGTH_SHORT)
                .show()
        }

        val adapter= ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        selectDeviceList.adapter = adapter
        selectDeviceList.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address : String = device.address

            val intent = Intent(this, ControlActivity::class.java)
            intent.putExtra(EXTRA_ADRESS, address)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLED_BLUETOOTH){
            if (m_bluetoothAdapter!!.isEnabled){
                Toast.makeText(applicationContext, "O Bluetooth já foi ativado", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(applicationContext, "O Bluetooth já foi desativado", Toast.LENGTH_SHORT)
                    .show()
            }
        } else if (resultCode == Activity.RESULT_CANCELED){
            Toast.makeText(applicationContext, "A ativação do Bluetooth foi cancelada", Toast.LENGTH_SHORT)
                .show()
        }

    }
}
