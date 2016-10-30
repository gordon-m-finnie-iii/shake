// sends http requests to the sysnet0 server and executes desired database operations via php scripts on sysnet0

public DBConnection{

    // URLS (sysnet0)
    private String POSTEVENT_urlstr = "sysnet0.cs.williams.edu/postevent.php";
    private String PAIR_urlstr = "sysnet0.cs.williams.edu/pair.php";
    private String LOGIN_urlstr = "sysnet0.cs.williams.edu/login.php";

    // types 
    private String POST = "POST";
    private String GET = "GET"; 

    // last message info 
    private String resp_code = "";
    private String json_rec = ""; 
    
    
    // Constructor  
    public DBConnection(){
	
    }

    /**
      * 
     * Connect to a URL 
     *
     **/
    private String sendMessage( String url_str , String type , ArrayList<NameValuePair> message_list ){

	// to be filled by server response 
	String receipt = "";

	// | = = = = = = = |
        // | S E N D |
        // | = = = = = = = |

	try{
	    // make url 
	    URL url = new URL(url_str);

	    // open connection
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 

	    // set parameters
	    conn.setRequestMethod(req_type);
            conn.setDoOutput(true);   

	    // send message
            OutputStream outStream = conn.getOutputStream();
            BufferedWriter outWriter = new BufferedWriter( new OutputStreamWriter(outStream, "UTF-8"));
            outWriter.write( encodeList(message_list) );

	    // terminate objects 
            outWriter.flush();
            outWriter.close();
            outStream.close();

	}catch( Exception ex1 ){

	    // log error
	    Log.i("dbc_sendMessage: ", "[Error 1] in server connection " + ex1.toString());	    

	}
	
	resp_code = conn.getResponseCode(); 

	// | = = = = = = = |
	// | R E C E I V E |
	// | = = = = = = = | 
	
	try{

	    // open reader
	    BufferedReader reader = new BufferedReader( new InputStreamReader(is,"iso-8859-1"),8);

	    // create string builder 
            StringBuilder sb = new StringBuilder();

            // build string
            String line = null;
            while((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }
            is.close();

            receipt = sb.toString();

	}catch( Exception ex2 ){

	    // log error 
	    Log.i("dbc_sendMessage: " , "[Error 2] in server receipt " + ex2.toString());
	    
	}
       	
	return receipt;
    }
    
    /***
     *
     * Add a user to the contact database at login
     *
     */
    public boolean login(String first, String last, String number){

	// create user profile to be added
	ArrayList<NameValuePair> userProfile = new ArrayList<NameValuePair>(); 
	userProfile.add( new BasicNameValuePair("first_name",first));
	userProfile.add( new BasicNameValuePair("last_name",last));
	userProfile.add( new BasicNameValuePair("phone_number", number));
	
	// send the request 
        String json_receipt = sendMessage( LOGIN_urlstr , POST , userProfile );
	
	
	// parse JSON 
	try{
	   
	    JSONArray jArray = new JSONArray(json_receipt); 
	    for(int i=0; i<jArray.length();i++){
		
		JSONObject json_data = jArray.getJSONObect(i);
		
		Log.i("dbc_addUser3" , "id: " + json_data.getInt("id"));
		      
	    }

	}catch( Exception ex ){

	    Log.e("dbc_adduser4" , "Error parsing JSON"+ex.toString()); 

	}
    }

    /**
     * 
     * post a contact sharing event to the database 
     *
     */
    public String postEvent(int TYPE, int time){
	// Calendar.getInstance().getTimeInMillis();
	
	// create user profile to be added
	ArrayList<NameValuePair> userProfile = new ArrayList<NameValuePair>();
	userProfile.add( new BasicNameValuePair("first_name",first));
	userProfile.add( new BasicNameValuePair("last_name",last));
	userProfile.add( new BasicNameValuePair("phone_number", number));
	

	// http posting
	try{
		
	    // prep connection 
	    URL url = new URL(postevent_url);
	    HttpURLConnection con = (HttpURLConnection) url.openConnection();
		
	    con.setRequestMethod("POST");
	    con.setDoOutput(true);

	    // send the event data
	    OutputStream outStream = con.getOutputStream(); 
	    BufferedWriter outWriter = new BufferedWriter( new OutputStreamWriter(outStream, "UTF-8"));
	    outWriter.write( encodeList(userProfile) );
	    outWriter.flush();
	    outWriter.close();
	    outStream.close(); 
	    
	    int responseCode = con.getResponseCode();
	    
	    // read response 
	    BufferedReader inReader = new BufferedReader( new InputStreamReader( con.getInputStream() ));
	    String inLine;
	    StringBuffer response = new StringBuffer();
	    
	    while((inputLine= inReader.readLine()) != null) {
		response.append(inLine);
	    }
	    
	    inReader.close();
	    
	}catch( Exception ex ){
	    Log.e("dbc_postevent: ", "Error in http connection " +ex.toString());
	}
	
	//parse JSON
	try{
	    JSONArray jArray = new JSONArray(response);
	    for(int i=0; i<jArray.length();i++){
		JSONObject json_data = jArray.getJSONObect(i);
		    //			Log.i("dbc_postevent" , "id: "+json_data.getInt("id")
	    }
	    
	}catch( Exception ex ){
		Log.e("dbc_postevent" , "Error parsing JSON"+ex.toString());
	}
	
    }
    
    /***
     *
     * flush variables 
     *
     */
    public void flush(){
	resp_code = ""; 
	json_receipt = "";
    }

    //
    // Pair: request a matching from the server until a timeout or a success
    //
    private boolean pair(int event_id){
	
	//
	boolean paired = false;
	
	//
	while( !paired ){
	    
	    // create user profile to be added
	    ArrayList<NameValuePair> pairingRequest = new ArrayList<NameValuePair>();
	    pairingRequest.add( new BasicNameValuePair("event_id",event_id));
	    
	    // http posting
	    try{
		
		// prep connection
		URL pair_url = new URL(urlstr_PAIR);
		HttpURLConnection conn = (HttpURLConnection) pair_url.openConnection();
		conn.setRequestMethod("GET");
		conn.setDoOutput(true);
		
		// send the event data
		OutputStream outStream = conn.getOutputStream();
		BufferedWriter outWriter = new BufferedWriter( new OutputStreamWriter(outStre \
										      am, "UTF-8"));
		outWriter.write( encodeList(userProfile) );
		outWriter.flush();
		outWriter.close();
		outStream.close();
		
		// check response
		int responseCode = con.getResponseCode();
		    
		// check error
		if( responseCode != 202){
		    Log.i("postEvent :", "non 202 HTTP response code --> " + responseCode);
		}else{

		    // read response
		    BufferedReader inReader = new BufferedReader( new InputStreamReader( con.getInputStream()));
			
		    String inLine;
		    StringBuffer response = new StringBuffer();
			
		    while((inputLine= inReader.readLine()) != null) {
			response.append(inLine);
		    }
		    
		    inReader.close();

		}catch( Exception ex ){
		    Log.e("dbc_postevent: ", "Error in http connection " +ex.toString());
		}

		// parse JSON
		try{
		    JSONArray jArray = new JSONArray(response);
		    for(int i=0; i<jArray.length();i++){
			JSONObject json_data = jArray.getJSONObect(i);
			//                  Log.i("dbc_postevent" , "id: "+json_data.getInt("id")
		    }
		    
		}catch( Exception ex ){
		    Log.e("dbc_postevent" , "Error parsing JSON"+ex.toString());
		}
	    }
	    
	}
    }
    
    private int postEvent( int t ){
	
	// create event as associative list
	ArrayList<NameValuePair> event = new ArrayList<NameValuePair>();
	event.add( new BasicNameValuePair("time", t));
	
	// post event
        try{
	    
            // prep connection
            URL events_url = new URL( urlstring_EVENTS );
            HttpURLConnection conn = (HttpURLConnection) events_url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // output the event
            OutputStream outStream = conn.getOutputStream();
            BufferedWriter outWriter = new BufferedWriter( new OutputStreamWriter(outStream, "UTF-8"));
            outWriter.write( encodeList(userProfile) );
            outWriter.flush();
            outWriter.close();
            outStream.close();

            // get response code from server
            int responseCode = conn.getResponseCode();

            // check
            if( responseCode != 202 ){

                Log.i("postEvent : ", "NON 202 RESPONSE CODE -> " + responseCode );
                return -1;

            }else{
		
                // read server response (as JSON format string)
                BufferedReader inReader = new BufferedReader( new InputStreamReader( conn.getInputStream()));
                String inLine;
                StringBuffer response = new StringBuffer();

                while((inputLine= inReader.readLine()) != null) {
                    response.append(inLine);
                }

                inReader.close();

                //
                // Get contact information arrays from JSONarray
                //
                JSONArray jArray = new JSONArray(response);
                for(int i=0; i<jArray.length();i++){

                    JSONObject json_data = jArray.getJSONObect(i);
                    //get last name
                    //get first name
                    //get phone number


                }
            }

        }catch( Exception e){

        }


    }
    /*
     *
     * HELPER FUNCTIONS
     *
     *
     */
    
    /***
     * @param l : list of pairs which need to be encoded
     * @return encoding : the single string encoding of the list
     */
    public String encodeList( Arraylist<NameValuePair> l ) throws UnsupportedEncodingException{
	
	StringBuilder encoding = new StringBuilder();
	boolean first = true;
	
	for (NameValuePair pair : l) {
	    if (first) {
		first = false;
	    } else encoding.append("&");
	    
	    encoding.append(URLEncoder.encode(pair.getName(), "UTF-8"));
	    encoding.append("=");
	    encoding.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
	}
	
	return encoding.toString();
	
    }
}