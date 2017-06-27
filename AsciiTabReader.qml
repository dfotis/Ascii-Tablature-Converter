//=============================================================================
//  MuseScore
//  Linux Music Score Editor
//  $Id:$
//
//
//  Copyright (C)2012 Werner Schweer and others
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License version 2.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program; if not, write to the Free Software
//  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//=============================================================================

import QtQuick 2.0
import MuseScore 1.0
import QtQuick.Dialogs 1.0
import QtQuick.Controls 1.0
import FileIO 1.0

MuseScore {
      //id: ms
      menuPath: "Plugins.AsciiTabReader"
      version:  "2.0"
      description: "This plugin runs an external command. Probably this will only work on Linux.";
      pluginType: "dialog"

      id:window
      width:  900;
      height: 600;      
      onRun: {}

      function executeJarFile() {
            console.log(window.filePath);
            file.setSource("/usr/share/mscore-"+window.version+"/plugins");

            proc.start("java -jar "+window.filePath+"/AsciiToMusicXML.jar "+"/tmp/my_file.txt");
            var val = proc.waitForFinished(30000);
            if (val) {
                  console.log(proc.readAllStandardOutput());
                  window.readScore("/tmp/my_music.xml")
                  Qt.quit()
            }  
            return 0;
      }
      
      QProcess {
        id: proc
        }
     
      FileIO {
            id: file
            onError: console.log(msg)
      }

      FileIO {
        id: xmlFile
        source: tempPath() + "/my_file.xml"
        onError: console.log(msg)
      }

      FileIO {
        id: textFile
        source: tempPath() + "/my_file.txt"
        onError: console.log(msg)
      }

      FileIO {
        id: myFile
        onError: console.log(msg + "  Filename = " + myFile.source)
      }

      FileDialog {
        id: fileDialog
        title: qsTr("Please choose a file")
        onAccepted: {
            var filename = fileDialog.fileUrl
            //console.log("You chose: " + filename)

            if(filename){
                myFile.source = filename
                //read txt file and put it in the TextArea
                tabText.text = myFile.read()
                        
                }
            }
      }

    Label {
        id: textLabel
        wrapMode: Text.WordWrap
        text: qsTr("The ascii tab must follow the form of the example. \nExamlple:\ne 0--2--4--5--7--9--10--11--12--\nb -------------------9--10--------------\n...\n1) The letter in the front declares the tuning of each string\n2) Each number declares the fret of each string\n3) The bars between each number(note) declares the duration of each note\n\n Paste your Ascii Tablature here or click button to load a file.")
        font.pointSize:12
        anchors.left: window.left
        anchors.top: window.top
        anchors.leftMargin: 10
        anchors.topMargin: 10
        }


    TextArea {
        id:tabText
        anchors.top: textLabel.bottom
        anchors.left: window.left
        anchors.right: window.right
        anchors.bottom: buttonOpenFile.top
        anchors.topMargin: 10
        anchors.bottomMargin: 10
        anchors.leftMargin: 10
        anchors.rightMargin: 10
        width:parent.width
        height:400
        textFormat: TextEdit.PlainText
        font.pointSize: 13
        textMargin: 10
        wrapMode: TextEdit.Wrap
        font.family: "Monospace"
        }


    Button {
        id : buttonOpenFile
        text: qsTr("Open file")
        anchors.bottom: window.bottom
        anchors.left: tabText.left
        anchors.topMargin: 10
        anchors.bottomMargin: 10
        anchors.leftMargin: 10
        onClicked: {
            fileDialog.open();
            }
        }

    Button {
        id : buttonCancel
        text: qsTr("Cancel")
        anchors.bottom: window.bottom
        anchors.right: buttonConvert.left
        anchors.topMargin: 10
        anchors.bottomMargin: 10
        onClicked: {
                Qt.quit();
            }
        }

          
      

    Button {
        id : buttonConvert
        text: qsTr("Import")
        anchors.bottom: window.bottom
        anchors.right: tabText.right
        anchors.topMargin: 10
        anchors.bottomMargin: 10
        anchors.rightMargin: 10
        onClicked: {
            textFile.write(tabText.text)
            executeJarFile()
        }
      }

      

      //onRun: {
/*
            console.log(ms.filePath);
            file.setSource("/usr/share/mscore-"+ms.version+"/plugins");

            console.log("Executing java jar file...");
            proc.start("java -jar "+ms.filePath+"/AsciiToMusicXML.jar "+ms.filePath+"/tablature1.txt");
            var val = proc.waitForFinished(30000);
            if (val)
                  console.log(proc.readAllStandardOutput());
                  ms.readScore("/tmp/my_music.xml")
                  Qt.quit()
            */
            //}
           
            

            
      }
