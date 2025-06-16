package com.example.hrmanagement.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

const val TAG = "AppDataManager"

class AppDataManager {
    var firestoreDB: FirebaseFirestore
    private var listenerRegistration: ListenerRegistration? = null
    private var usersigninstatuslistenerRegistration: ListenerRegistration? = null
    private val _announcementLiveData = MutableLiveData<List<String>>()
    val announcementLiveData: LiveData<List<String>> = _announcementLiveData
    private var _liveUserSignInStatus: MutableStateFlow<String> = MutableStateFlow("Not populated yet!")
    val liveUserSignInStatus = _liveUserSignInStatus.asStateFlow()

    init {
        firestoreDB = Firebase.firestore
    }

    fun addGoogleAuthUserData(googleAuth: GoogleAuth){
        val usersCollection = firestoreDB.collection("users")
        usersCollection.document(googleAuth.email)
            .set(googleAuth)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID: ${googleAuth.email}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun addDummyUserSignInStatus(emailid: String,userSignInStatusData: UserSignInStatusData) {
        val signInStatusDocumentRef = firestoreDB.collection("signinstatus").document(emailid)
        signInStatusDocumentRef.set(userSignInStatusData)
    }

    fun addDummyUserData(userLoginData: UserLoginData) {
        val signInStatusDocumentRef = firestoreDB.collection("users").document(userLoginData.email.toString())
        signInStatusDocumentRef.set(userLoginData)
    }

    fun addDummyLinkData(linkData: LinkData) {
        val linkDocumentRef = firestoreDB.collection("quicklinks").document(linkData.linkname)
        linkDocumentRef.set(linkData)
    }

    fun addSignInStatus(emailId: String, checkInOrCheckOutTimestamp: Long, currentDayTimestamp: Long, responseHandler: (String,String) -> Unit){

        firestoreDB.runTransaction { transaction ->
            var userCurrentSignInStatus: UserSignInStatusData? = UserSignInStatusData()
            val signInStatusDocumentRef = firestoreDB.collection("signinstatus").document(emailId)
            val signInStatusDocument = transaction.get(signInStatusDocumentRef)
            if (signInStatusDocument.exists()) {
                userCurrentSignInStatus = signInStatusDocument.toObject(UserSignInStatusData::class.java)
                println("Field value: ${userCurrentSignInStatus?.status}")
            } else {
                responseHandler("Failure","No such SIGN-IN document")
            }
            userCurrentSignInStatus?.let { userSignInStatus ->
                var userAttendanceLog: AttendanceData? = AttendanceData()
                val attendanceDocumentCollectionRef = firestoreDB.collection("attendance").document(emailId).collection("attendanceLogs").document(currentDayTimestamp.toString())
                val attendanceLogDocument = transaction.get(attendanceDocumentCollectionRef)

                if (attendanceLogDocument.exists()) {
                    userAttendanceLog = attendanceLogDocument.toObject(AttendanceData::class.java)
                } else {
                    Log.d("AppDataManager","No such ATTENDANCE LOG document")
//                    responseHandler("Failure","No such ATTENDANCE LOG document")
                    val timestamp: Long = 1622548800000
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = currentDayTimestamp
                    userAttendanceLog = AttendanceData(
                        currentDayTimestamp,
                        0,
                        0,
                        "",
                        "",
                        "",
                        "",
                        emailId,
                        "",
                        0.0f,
                        calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.YEAR),
                        ""
                    )
                }
                userAttendanceLog?.let { attendanceLog ->
                    if (userSignInStatus.status=="Checked-In"){
                        val diffMillis = checkInOrCheckOutTimestamp - attendanceLog.checkInTime
                        val hours = diffMillis / (1000 * 60 * 60)
                        val minutes = (diffMillis % (1000 * 60 * 60)) / (1000 * 60)
                        userSignInStatus.checkouttimestamp = checkInOrCheckOutTimestamp
                        userSignInStatus.status = "Checked-Out"
                        attendanceLog.checkOutTime = checkInOrCheckOutTimestamp
                        attendanceLog.checkOutLocation = "Chennai"
                        attendanceLog.totalHours = "$hours.$minutes".toFloat()
                    } else {
                        if ( attendanceLog.checkInTime == 0L ) { //will be executed when the user checks-in for the first time on a particular day
                            userSignInStatus.checkintimestamp = checkInOrCheckOutTimestamp
                            attendanceLog.checkInTime = checkInOrCheckOutTimestamp
                            attendanceLog.status = "Present"
                        }
                        userSignInStatus.status = "Checked-In"
                        attendanceLog.checkInLocation = "Chennai"
                    }
                    transaction.set(signInStatusDocumentRef,userSignInStatus)
                    transaction.set(attendanceDocumentCollectionRef,attendanceLog)
                }
            }
        }.addOnSuccessListener { result ->
            Log.d(TAG, "Success $result")
            responseHandler("Success","")
        }.addOnFailureListener { exception ->
                responseHandler("Failure","Error transcation failed: $exception")
        }
    }

    fun getUserGoalsData(emailId: String, responseHandler: (QuerySnapshot?,String) -> Unit) {
        val goalsCollection = firestoreDB.collection("goals").document(emailId).collection("indigoals")
        goalsCollection.get()
            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
                responseHandler(result,"Success")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting goals: ", exception)
                responseHandler(null,"Failure")
            }
    }

    fun getAllQuickLinks(responseHandler: (QuerySnapshot?,String) -> Unit) {
        val quickLinksCollection = firestoreDB.collection("quicklinks")
        quickLinksCollection.get()
            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
                responseHandler(result,"Success")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting goals: ", exception)
                responseHandler(null,"Failure")
            }
    }

    fun getLimitedQuickLinks(limit: Long, responseHandler: (QuerySnapshot?,String) -> Unit) {
        val quickLinksCollection = firestoreDB.collection("quicklinks")
        val limitedQuery = quickLinksCollection
            .orderBy("linkname", Query.Direction.DESCENDING)
            .limit(limit)

        limitedQuery.get()
            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
                responseHandler(result,"Success")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting goals: ", exception)
                responseHandler(null,"Failure")
            }
    }

    fun addRegularisationAttendanceData(emailId: String,attendanceData: List<AttendanceRegularisationData>, returnResponse: (String) -> Unit){
        val batch = firestoreDB.batch()
        val attendanceCollection = firestoreDB.collection("attendance").document(emailId).collection("attendanceRegularisationLogs")
        for (data in attendanceData) {
            val docRef = attendanceCollection.document(data.date.toString()) // auto-generated ID
            batch.set(docRef, data)
        }
        batch.commit()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID: ${emailId}")
                returnResponse("Success")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                returnResponse("Failure")
            }
    }

    fun getFirebaseAttendanceData(startDateTimestamp: Long,endDateTimestamp: Long, emailId: String,responseHandler: (QuerySnapshot?,String) -> Unit){
        val attendanceCollection = firestoreDB.collection("attendance").document(emailId).collection("attendanceLogs")
        val query = attendanceCollection
            .whereLessThanOrEqualTo("date", endDateTimestamp)
            .whereGreaterThanOrEqualTo("date", startDateTimestamp)
        query.get()
            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
                responseHandler(result,"Success")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting attendance record: ", exception)
                responseHandler(null,"Failure")
            }
    }

    fun addGoalData(goalData: GoalData,lastGoalIndex: Int,updateLastGoalIndex: Boolean, returnResponse: (String) -> Unit){
        firestoreDB.runBatch { batch ->
            val goalDocument = firestoreDB.collection("goals").document(goalData.emailId).collection("indigoals").document("goal$lastGoalIndex")
            batch.set(goalDocument,goalData)
            if (updateLastGoalIndex) {
                val parentGoalCollectionDoc = firestoreDB.collection("goals").document(goalData.emailId)
                batch.update(parentGoalCollectionDoc, "lastGoalIndex", lastGoalIndex)
            }
        }.addOnSuccessListener {
            Log.d(TAG, "DocumentSnapshot goal added/updated with ID: goal$lastGoalIndex")
            returnResponse("Success")
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error adding goal", e)
            returnResponse("Failure")
        }
    }

    fun addDepartmentData(departmentInfo: DepartmentInfo){
        val usersCollection = firestoreDB.collection("departments").document("Department1").collection("subDepartments").document("Sub1Department1").collection("subDepartments")
        usersCollection.document(departmentInfo.name)
            .set(departmentInfo)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ID: ${departmentInfo.name}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun getUserLastFeedData(emaailId: String,successResponse: (FeedMetadata?, String) -> Unit){
        val docRef = firestoreDB.collection("feeds").document(emaailId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val resultData = document.toObject(FeedMetadata::class.java)
                    resultData?.let {
                        Log.d(TAG, "getFirebaseDepartment temp data $it")
                        successResponse(it,"Success")
                    }
                } else {
                    Log.d(TAG, "No such document")
                    successResponse(null,"No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
                successResponse(null,"Error Exception $exception")
            }
    }

    fun addUserStatusData(feedData: FeedData,updateLastFeedIndex: Boolean,feedCount: Int, returnResponse: (String) -> Unit){
        firestoreDB.runBatch { batch ->
            val feedDocumentRef = firestoreDB.collection("feeds").document(feedData.email).collection("userfeeds").document(feedData.feedID)
            batch.set(feedDocumentRef,feedData)
            val parentFeedCollectionDoc =
                firestoreDB.collection("feeds").document(feedData.email)
            if (updateLastFeedIndex) {
                batch.update(parentFeedCollectionDoc, "lastFeedId", feedData.feedID.toInt())
            }
            batch.update(parentFeedCollectionDoc, "feedCount", feedCount)
            val signInStatusCollectionDoc =
                firestoreDB.collection("signinstatus").document(feedData.email)
            batch.update(signInStatusCollectionDoc, "userStatus", feedData.message)
        }.addOnSuccessListener {
            Log.d(TAG, "DocumentSnapshot goal added/updated with ID: goal$updateLastFeedIndex")
            returnResponse("Success")
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error adding goal", e)
            returnResponse("Failure")
        }
    }

    fun addLeaveTrackerData(leaveTrackerData: LeaveTrackerData,year: Int, returnResponse: (String) -> Unit){
        val usersCollection = firestoreDB.collection("leavetrackers")
        usersCollection.document("${leaveTrackerData.emailId}$year")
            .set(leaveTrackerData)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added/updated with ID: ${leaveTrackerData.emailId}$year")
                returnResponse("Success")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                returnResponse("Failure")
            }
    }

    fun addLeaveTrackerDataTemp(leaveTrackerData: LeaveTrackerData,year: Int){
        val usersCollection = firestoreDB.collection("leavetrackers")
        usersCollection.document("${leaveTrackerData.emailId}$year")
            .set(leaveTrackerData)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added/updated with ID: ${leaveTrackerData.emailId}$year")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun getFirebaseLeaveTrackerData(year: Int,emailId: String,successResponse: (LeaveTrackerData, String) -> Unit){
        var resultData: LeaveTrackerData? = LeaveTrackerData()
        val docRef = firestoreDB.collection("leavetrackers").document("$emailId$year")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    resultData = document.toObject(LeaveTrackerData::class.java)
                    resultData?.let {
                        Log.d(TAG, "getFirebaseDepartment temp data $it")
                        successResponse(it,"Success")
                    }
                } else {
                    Log.d(TAG, "No such document")
                    successResponse(resultData?: LeaveTrackerData(),"No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
                successResponse(resultData?: LeaveTrackerData(),"Error Exception $exception")
            }
    }

    fun getFirebaseDepartment(depatmentId: String,responseHandler: (QuerySnapshot?, String) -> Unit) {
        val usersCollection = firestoreDB.collection("users")
        val query = usersCollection
            .whereEqualTo("departmentName", depatmentId)
        query.get()
            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
                responseHandler(result,"Success")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting goals: ", exception)
                responseHandler(null,"Failure")
            }
    }

    fun getFirebaseDepartmentOld(depatmentId: String,successResponse: (DepartmentInfo?,String) -> Unit) {
        var resultData: DepartmentInfo? = DepartmentInfo()
        val docRef = firestoreDB.collection("departments").document(depatmentId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    resultData = document.toObject(DepartmentInfo::class.java)
                    resultData?.let {
                        Log.d(TAG, "getFirebaseDepartment temp data $it")
                        successResponse(it,"Success")
                    }
                } else {
                    Log.d(TAG, "No such document")
                    successResponse(null,"No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
                successResponse(null,"Error Exception $exception")
            }
    }

    fun getFirebaseUser(userid: String,successResponse: (UserLoginData?, String) -> Unit) {
        var resultData: UserLoginData? = UserLoginData()
        val docRef = firestoreDB.collection("users").document(userid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    resultData = document.toObject(UserLoginData::class.java)
                    resultData?.let {
                        Log.d(TAG, "getFirebaseUser temp data $it")
                        successResponse(it,"Success")
                    }
                } else {
                    Log.d(TAG, "No such document")
                    successResponse(null,"No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
                successResponse(null, "Error Exception $exception")
            }
    }

    fun getAllFirebaseUsers(successResponse: (QuerySnapshot?, String) -> Unit) {
        val usersCollectionReference = firestoreDB.collection("users")
        usersCollectionReference.get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot != null) {
                    Log.d(TAG, "getAllFirebaseUsers temp data ${querySnapshot.size()}")
                    successResponse(querySnapshot,"Success")
                } else {
                    Log.d(TAG, "getAllFirebaseUsers No such document")
                    successResponse(null,"getAllFirebaseUsers No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
                successResponse(null, "Error Exception $exception")
            }
    }

    fun listenForUserSignInStatusUpdates(userid: String){
        usersigninstatuslistenerRegistration = firestoreDB.collection("signinstatus").document(userid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                val source = if (snapshot != null && snapshot.metadata.hasPendingWrites()) {
                    "Local"
                } else {
                    "Server"
                }
                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "listenForUserSignInStatusUpdates userid - $userid data ${snapshot.data}")
                    val temp = snapshot.toObject(UserSignInStatusData::class.java)
                    Log.d(TAG, "listenForUserSignInStatusUpdates temp data $temp")
                    if (!temp?.status.isNullOrBlank()){
                        _liveUserSignInStatus.value = temp.status.toString()
                    }
                } else {
                    Log.d(TAG, "$source data: null")
                }
            }
    }

    fun listenForUserUpdates() {
        listenerRegistration = firestoreDB.collection("users")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    for (docChange in snapshots.documentChanges) {
                        when (docChange.type) {
                            DocumentChange.Type.ADDED -> {
                                // Handle new document
                                docChange.document
                            }
                            DocumentChange.Type.MODIFIED -> {
                                // Handle modified document
                            }
                            DocumentChange.Type.REMOVED -> {
                                // Handle removed document
                            }
                        }
                    }
                }
            }
    }

    fun onStop() {
        listenerRegistration?.remove()
    }

    fun getLimitedAnnouncements(limit: Long, responseHandler: (QuerySnapshot?,String) -> Unit) {
        val announcementsCollection = firestoreDB.collection("announcements").document("organization1").collection("OrgAnnouncements")
        val limitedQuery = announcementsCollection
            .orderBy("announcementID", Query.Direction.DESCENDING)
            .limit(limit)

        limitedQuery.get()
            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
                responseHandler(result,"Success")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting goals: ", exception)
                responseHandler(null,"Failure")
            }
    }

    fun getAnnouncements(responseHandler: (QuerySnapshot?,String) -> Unit) {
        val announcementsCollection = firestoreDB.collection("announcements").document("organization1").collection("OrgAnnouncements")
        announcementsCollection.get()
            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
                responseHandler(result,"Success")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting goals: ", exception)
                responseHandler(null,"Failure")
            }
    }

    fun getAnnouncementData(announcementId: Int, responseHandler: (DocumentSnapshot?, String) -> Unit) {
        val announcementsDocument = firestoreDB.collection("announcements").document("organization1").collection("OrgAnnouncements").document(announcementId.toString()).collection("AnnouncementData").document(announcementId.toString())
        announcementsDocument.get()
            .addOnSuccessListener { result ->
                responseHandler(result,"Success")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting goals: ", exception)
                responseHandler(null,"Failure")
            }
    }

    fun modifyAnnouncementData(announcementData: AnnouncementData, returnResponse: (String) -> Unit){
        val announcementsDocument = firestoreDB.collection("announcements").document("organization1").collection("OrgAnnouncements").document(announcementData.announcementID.toString()).collection("AnnouncementData").document(announcementData.announcementID.toString())
        announcementsDocument
            .set(announcementData)
            .addOnSuccessListener {
                Log.d(TAG, "addAnnouncementLikeData added/updated with ID: ${announcementData.announcementID}")
                returnResponse("Success")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                returnResponse("Failure")
            }
    }

    fun addCommentLikeData(commentsData: MutableMap<String, CommentsData>, announcementID: Int, returnResponse: (String) -> Unit){
        val announcementsDocument = firestoreDB.collection("announcements").document("organization1").collection("OrgAnnouncements").document(announcementID.toString()).collection("AnnouncementData").document(announcementID.toString())
        announcementsDocument
            .update("comments",commentsData)
            .addOnSuccessListener {
                Log.d(TAG, "addCommentLikeData added/updated with ID: ${commentsData}")
                returnResponse("Success")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                returnResponse("Failure")
            }
    }

    fun getNotificationData(responseHandler: (QuerySnapshot?,String) -> Unit){
        val notificationCollection = firestoreDB.collection("notifications").document("organization1").collection("OrgNotifications")
        notificationCollection.get()
            .addOnSuccessListener { result ->
                responseHandler(result,"Success")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting goals: ", exception)
                responseHandler(null,"Failure")
            }
    }

    fun addAnnouncementTemp(announcementList: AnnouncementList){
        val announcementsCollection = firestoreDB.collection("announcements").document("organization1").collection("OrgAnnouncements")
        announcementsCollection.document("${announcementList.announcementID}")
            .set(announcementList)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added/updated with ID: ${announcementList.announcementID}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun addAnnouncementDataTemp(announcementData: AnnouncementData){
        val announcementsCollection = firestoreDB.collection("announcements").document("organization1").collection("OrgAnnouncements").document("${announcementData.announcementID}").collection("AnnouncementData")
        announcementsCollection.document("${announcementData.announcementID}")
            .set(announcementData)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added/updated with ID: ${announcementData.announcementID}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun addNotificationDataTemp(notificationData: NotificationData){
        val notificationDocumentRef = firestoreDB.collection("notifications").document("organization1").collection("OrgNotifications").document("${notificationData.notificationId}")
        notificationDocumentRef
            .set(notificationData)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added/updated with ID: ${notificationData.notificationId}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun getHolidays(responseHandler: (QuerySnapshot?,String) -> Unit) {
        val holidaysCollection = firestoreDB.collection("holidays")
        holidaysCollection.get()
            .addOnSuccessListener { result ->
                responseHandler(result,"Success")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting goals: ", exception)
                responseHandler(null,"Failure")
            }
    }


}

/*

                            .filter { it ->
                                it.key == "content"
                            }
                            .toMutableMap()

     //        val json = Json.encodeToString(googleAuth)
//        val userMap = Json.decodeFromString<Map<String, String>>(json)

                        document.data?.forEach { key,value ->
//                        val regex = """\s*([^=,{]+)=([^,}]+)\s*""".toRegex()
//                        regex.findAll(value.toString())
//                            .associate {
//                                val (key, value) = it.destructured
//                                if(((key.trim())=="content") && (value.trim().isNotBlank())) {
//                                    basemap.put(key,value.trim())
//                                } else {
//                                    basemap.put(key,"")
//                                }
//                                key.trim() to value.trim()
//                            }
//                    }
//                    basemap.put("AppStatus","Success")
//                    Log.d(TAG, "basemap data ${basemap}")


//    fun addAnnualLeaveData(leaveTrackerData: LeaveTrackerData,year: Int, reutrnResponse: (String) -> Unit){
//        var operationStatus = "Failure"
//        val usersLeaveTrackerDocument = firestoreDB.collection("leavetrackers").document("${leaveTrackerData.emailId}${year}")
//        usersLeaveTrackerDocument.update(
//            "annualLeaveData",updatedAnnualLeaveData,
//            "lastLeaveId",lastLeaveID)
//            .addOnSuccessListener {
//                Log.d(TAG, "DocumentSnapshot successfully updated!")
//                operationStatus = "Success"
//                reutrnResponse(operationStatus)
//            }
//            .addOnFailureListener { e ->
//                Log.w(TAG, "Error updating document", e)
//                operationStatus = "Failure"
//                reutrnResponse(operationStatus)
//            }
//    }

 */