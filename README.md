# Demo-voice-chatbot
Demonstrating Google Assistant and Siri <br/>
This project demonstrates interation with these chatbots over voice.
Please note, audio functions in Perfecto require separate licensing.
The input is text. Perfecto will translate the text into voice and inject to the device.
Perfecto will record the audio response and wait for visual match of the expected result.
Perfecto will then convert the recorded audio to text and conduct the relevant validations.
In this approach it is possible to opt to conduct textual validations on the screen and measure response times, and/or add validation of the spoken response.
 

Notes:

- Select the device of choice in testNG.xml

- Ensure you have the environment variables setup correctly

- Siri integration requires more attention, will be fixed soon

Functions of interest:

- SetDictionary sets the voice to inject, the text and visual to validate, separate for Google and Siri

- testChatbotFlowViaT2S does the complete flow