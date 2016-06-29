#include <SoftwareSerial.h>
#include <IRremote.h>

SoftwareSerial BT(10, 11);
IRsend irsend;
int count = 0; 
const String varControle = "CONTROLE";
int LEDBLUETOOTH = 12;
int LEDLEITOR = 12; 
int inPin = 3;
int RECV_PIN = 13; 
String leituraControle;
IRrecv irrecv(RECV_PIN);
decode_results results;
bool configControle = false; 
bool enviarValores = false; 

void setup()  
{
  /**********************************/
  /* PREPARANDO BLUETOOTH  */     
  BT.begin(9600);
  pinMode(LEDBLUETOOTH, OUTPUT);    
  /**********************************/
  /* PREPARANDO IR  */     
  irrecv.enableIRIn();  
  Serial.begin(9600);
  /**********************************/  
  pinMode(inPin, INPUT);    
  Serial.println("pronto para guerra");
}

void dump(decode_results *results)
{
  count = results->rawlen;
  if(results->bits > 0)
  {
      leituraControle =  String(results->value, HEX);      
      BT.println(leituraControle);
      configControle = false;   
  }
}

void acendeLed(int valorLed,int TempoDelay)
{
    digitalWrite (valorLed, HIGH);
    delay (TempoDelay); 
    digitalWrite (valorLed, LOW);     
}  
String str;

void gerenciaBluetooth()
{   
    if (BT.available())
    { 
      str = BT.readString();
      str.trim();
      Serial.println("GerenciaBluetooth"+ str);
      if(str.equals(varControle))
      {
           configControle = true;  
           acendeLed(LEDBLUETOOTH,2000);
           enviarValores = false; 
      }else{
           configControle = false;
           acendeLed(LEDBLUETOOTH,500);
           enviarValores = false;   
           const char *cstr = str.c_str();     
           long unsigned valorConvertidoLong = strtoul(cstr, 0, 16);
           irsend.sendNEC(valorConvertidoLong, 32); 
           BT.println("ENVIADO");              
      }          
    } 
}
void gerenciaControleRemoto()
{
  if (irrecv.decode(&results)) 
  {
      dump(&results);
      irrecv.resume(); 
  }
} 

void loop() 
{      
    if(configControle)
    { 
       gerenciaControleRemoto(); 
    }else{
       gerenciaBluetooth();  
    } 
}

