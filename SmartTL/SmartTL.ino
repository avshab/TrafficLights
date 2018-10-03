#include "SoftwareSerial.h"
SoftwareSerial BTserial(8, 9);

#define PIN_BTN_RED 12
#define PIN_BTN_YELLOW 11
#define PIN_BTN_GREEN 10
#define PIN_SWITCH_1 7
char val[3];

void setup()
{
    Serial.begin(9600);
    BTserial.begin(9600);

    pinMode(PIN_BTN_RED, OUTPUT);
    pinMode(PIN_BTN_YELLOW, OUTPUT);
    pinMode(PIN_BTN_GREEN, OUTPUT);
    pinMode(PIN_SWITCH_1, OUTPUT);

}
char s;
char a;
int count = 0;
void loop()
{
  if(BTserial.available()){
    a = BTserial.read();
    Serial.println(a);

    if (a == '+' && count == 0) {
      //если он равен @ (случайно выбранный мною символ)
      //обнуляем переменную val
      val[0] = 0;
      val[1] = 0;
      val[2] = 0;
      count = 0;
      a = ' ';
      Serial.println("return");
      return;
    } 
      
    val[count] = a;
    a = ' ';
    Serial.print(count);
    Serial.print(" = ");
    Serial.print(val[count]);
    Serial.println("");
    count++;
  
    if(count != 3){
      Serial.println("count != 3 return");
      return;
    }
    if(val[0] == ' ' || val[1] == ' ' || val[2] == ' '){
      Serial.println("null values");
      return;
    }
      
    Serial.println("do switch");
    digitalWrite(PIN_BTN_RED, val[0]=='0'?LOW:HIGH);
    digitalWrite(PIN_BTN_YELLOW, val[1]=='0'?LOW:HIGH);
    digitalWrite(PIN_BTN_GREEN,val[2]=='0'?LOW:HIGH);
    a = ' ';
    count = 0;
  }
  
 if(Serial.available()){   
    s = Serial.read();
    Serial.println(s);
  }
  delay(10);
}
