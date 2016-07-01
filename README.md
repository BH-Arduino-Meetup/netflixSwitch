# Netflix Switch
Em 13 de Fevereiro de 2016, nós do BH Arduiners nos reunimos em um Meetup, com a intenção de construir um Gadget anunciado pela empresa NetFlix, chamado [NetFlix Switch](http://makeit.netflix.com/the-switch), que utilizava diversas tecnologias. Entre elas o Arduíno.

![alt text](https://cezarantsouza.files.wordpress.com/2016/06/botao.jpg "NetFlix Switch")
<br>
Por diversos motivos, o dispositivo não foi possível de ser construído nos mesmos moldes propostos pela empresa. Porém, com componentes mais simples, modificações no código-fonte original e muita criatividade, conseguimos chegar a uma versão alternativa que além de atender aos requisitos propostos, também proporcionou bastante conhecimento para todos os envolvidos.  
<br> 
O projeto abrange 3 áreas de conhecimento para ser realizado: mecânica/prototipagem, eletrônica e programação mobile. 
Abaixo iremos listar os procedimentos utilizados em cada etapa e todo o código fonte utilizado no Arduíno e no projeto mobile pode ser encontrado neste repositório através dos diretórios : <b>Android_src</b> e <b>codigoFonteArduino</b>.

# Mecânica/Prototipagem 
O primeiro protótipo do Gadget foi apresentado durante o [Arduino Day 2016](https://day.arduino.cc/#/) e verificamos que dada a quantidade de componentes utilizados, inseridos numa protoboard, abrigar todos numa caixa seria um dos grandes desafios do projeto.   
![alt text](https://cezarantsouza.files.wordpress.com/2016/06/componentes.jpg "Protoboard - Primeira versão")
Durante o evento, com diversas sugestões da Designer [Katarine Inis](https://www.facebook.com/katarine.inis) fomos começando a desenvolver alternativas para abrigar os componentes de forma organizada e definindo algumas premissas próprias como: 

1. Deixar os componentes a vista no projeto, evidênciando de forma lúdica a eletrônica envolvida 
2. Utilizar peças e modelagem 3d para montagem do projeto  
3. Não fazer uso de placas de circuito impresso (PCB), para não aumentar a complexidade e o fazer o mesmo se tornar menos acessível

Seguindo estas e outras premissas e baseado no modelo do Fritzen listado abaixo, o [Igor Carmo](https://www.facebook.com/igor.carmo.16) iniciou o desenvolvimento de uma caixinha utilizando peças de acrílico e uma uma protoboard reduzida.
![alt text](https://cezarantsouza.files.wordpress.com/2016/06/protoboard.jpg "Fritzen")

Caixa de Acrílico sendo desenvolvida. 
![alt text](https://cezarantsouza.files.wordpress.com/2016/06/img-20160605-wa0017.jpg "Versão final")

Depois de mais alguns Brainstorms durante a construção, no dia 25/06/2016 durante o primeiro Meetup do BH Arduiners no FabLab,finalmentefoi apresentado a todos o resultado final do Gadget.   

Além do resultado do protótipo físico, foi gerado também um documento super completo, com detalhamento de Data Sheets, dimensões do protótipo e todas as etapas de modelagem envolvidas. Este documento pode ser acessado [neste link ](https://cezarantsouza.files.wordpress.com/2016/06/relatc3b3rionetflix.pdf) e só ajudou a enriquecer ainda mais o projeto. 
<br>
<br>
Apesar de todo o processo ter sido realizado em conjunto com a participação de todos, acredito que maiores informações técnicas a respeito desta parte envolvendo mecânica e prototipagem podem ser retiradas com Igor e com a Katarina.

# Eletrônica 

Na primeira reunião, começamos a parte eletrõnica seguindo as orientações da NetFlix. Utilizamos uma placa ESP 8266 para realizar a comunicação e a única alteração prevista no projeto seria retirar o comunicação com a pizzaria Domino's, presente na idéia original. O App Android também seria simplificado para fazer uma Requisição Http na ESP 8266 e entrar em silencioso logo após.  
![alt text](https://cezarantsouza.files.wordpress.com/2016/06/primeira.jpg "Primeira versão do projeto")
Porém após alguns brainstorms entre os envolvidos ([Matheus  Cavalieri](https://www.facebook.com/matheuscavalieribh), [Cézar Antônio](https://www.facebook.com/cezar.a.desouza), [Carla Queiroga Werkhaizer](https://www.facebook.com/carla.werkhaizer) e [Júlio César Carneiro](https://www.facebook.com/jcca007) algumas coisas foram alteradas:

1.A ESP 8266 foi substituída por um HC-06  
2.Um relay foi incluído no projeto, substituíndo as lampadas [Philips Hue](http://www2.meethue.com/en-us/) sugeridas pelo projeto original

O código-fonte do arduíno ficou bastante enxuto e trabalha em conjunto com o aplicativo Android. Para carregá-lo não é necessário nenhuma biblioteca que já não esteja instalada no IDE padrão do Arduíno.

Já a montagem, pode ser feita com um Arduíno Uno e segue um esquema parecido com o mostrado abaixo, com poucas variações.
![alt text](https://cezarantsouza.files.wordpress.com/2016/06/protoboard.jpg "Fritzen")

Pra nossa felicidade, tudo que diz respeito a Arduíno no projeto acabou se tornando bastante acessível e intuitivo, seguindo as premissas do Hardware Aberto. 

# Desenvolvimento Android 

Para o desenvolvimento do App Android, foi utilizado o Android Studio. O que infelizmente, pela montagem de ambiente um pouco trabalhosa, pode exigir uma relativa curva de aprendizado na sua construção. Apesar disso, esta foi a parte que talvez, tenha sido aproveitada a maior quantidade do material original disponibilizado pela NetFlix. 
<br>
<br>
Para a comunicação com o Gadget através do Bluetooth foram feitas algumas adaptações do código-fonte encontrado neste [repositório](https://github.com/janosgyerik/bluetoothviewer).(Inclusive se alguém tiver conhecimento de Licenças e puder possa nos ajudar a esclarecer se isso viola alguma lei de CopyRight, nos ajudaria bastante)

Basicamente o funcionamento do app é bem simples.

<br>Primeiramente o usuário abre o aplicativo que já requisita uma conexão com o bluetooth.<br> 
![alt text](https://cezarantsouza.files.wordpress.com/2016/06/21.png "Inicial")
<br>Logo após o usuário é orientado a cadastrar os botões da sua televisão.<br>
![alt text](https://cezarantsouza.files.wordpress.com/2016/06/8.png  "Cadastro")
<br>Após o cadastro de todos os botões o sistema está pronto para realizar todas as operações.<br> 
![alt text](https://cezarantsouza.files.wordpress.com/2016/06/111.png "Pronto")
<br>Após toda a operação, o Smarthphone é colocado em modo silencioso.<br> 
![alt text](https://cezarantsouza.files.wordpress.com/2016/06/13.png "Silencioso")

Apesar de ter tido a participação de todos, o [Cézar Antônio](http://github.com/cezarant) pode ajudar a esclarecer detalhes a respeito das modificações que foram feitas no código-fonte Android original.

# Resumo de Links

1. [BH Arduiners](https://www.facebook.com/groups/meetuparduiners/)
2. [NetFlix Switch](http://makeit.netflix.com/the-switch) 
3. [Manual detalhado da construção das peças](https://cezarantsouza.files.wordpress.com/2016/06/relatc3b3rionetflix.pdf)
4. [Library Bluetooth Utilizada no Projeto Android](https://github.com/janosgyerik/bluetoothviewer)
5. [Cézar Antônio](http://github.com/cezarant)
6. [Matheus  Cavalieri](https://www.facebook.com/matheuscavalieribh)
7. [Júlio César Carneiro](https://www.facebook.com/jcca007)
8. [Carla Queiroga Werkhaizer](https://www.facebook.com/carla.werkhaizer)
9. [Katarine Inis](https://www.facebook.com/katarine.inis)
10. [Igor Carmo](https://www.facebook.com/igor.carmo.16)

# Contribuições 
Há algumas melhorias que podem ser implementadas e todas elas estarão sugeridas na seção <b>Issues</b> deste repositório. 
<br>
Sinta-se a vontade também para entrar em contato conosco através do nosso [grupo do Facebook](https://www.facebook.com/groups/meetuparduiners/).

# Agradecimento
Agradecemos a participação de todos os participantes envolvidos e parceiros e ficamos na expectativa de enfrentar mais desafios tecnológicos tão enriquecedores quanto este.
