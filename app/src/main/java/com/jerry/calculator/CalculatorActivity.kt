package com.jerry.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_calculator.*

class CalculatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)
    }
    fun buSelected(view: View){
        if (oper==true){
            ShowNumber.setText("")
        }
        oper =false
        val buselect= view as Button
        var buClickValue=ShowNumber.text.toString()
        when(buselect.id){
            bu0.id->{
                buClickValue +="0"
            }
            bu1.id->{
                buClickValue +="1"
            }
            bu2.id->{
                buClickValue +="2"
            }
            bu3.id->{
                buClickValue +="3"
            }
            bu4.id->{
                buClickValue +="4"
            }
            bu5.id->{
                buClickValue +="5"
            }
            bu6.id->{
                buClickValue +="6"
            }
            bu7.id->{
                buClickValue +="7"
            }
            bu8.id->{
                buClickValue +="8"
            }
            bu9.id->{
                buClickValue +="9"
            }
            budott.id->{
                buClickValue +="."
            }
            buPlusMinus.id->{
                buClickValue ="-" + buClickValue
            }
        }
        ShowNumber.setText(buClickValue)
    }

    var op="*"
    var oldNum=""
    var oper = true
    fun bumath(view: View){

        val buSelect = view as Button
        when(buSelect.id){

            bumul.id->{
                op="*"
            }
            busum.id->{
                op="+"
            }
            bumin.id->{
                op="-"
            }
            budiv.id-> {
                op = "/"
            }

        }
        oldNum= ShowNumber.text.toString()
        oper=true



    }
    fun buEqual(view: View){
        val newNumber=ShowNumber.text.toString()
        var finalNumber:Double?=null
        when(op){
            "*"->{
                finalNumber= oldNum.toDouble() * newNumber.toDouble()
            }
            "/"->{
                finalNumber= oldNum.toDouble() / newNumber.toDouble()
            }
            "+"->{
                finalNumber= oldNum.toDouble() + newNumber.toDouble()
            }
            "-"->{
                finalNumber= oldNum.toDouble() - newNumber.toDouble()
            }

        }
        ShowNumber.setText(finalNumber.toString())
        oper=true
    }

    fun buPercent(view: View){
        var number=ShowNumber.text.toString().toDouble()
        ShowNumber.setText(number.toInt())
        oper=true
    }

    fun buAc(view: View){
        ShowNumber.setText("0")
        oper=true
    }

}