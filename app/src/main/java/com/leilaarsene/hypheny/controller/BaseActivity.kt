import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.leilaarsene.hypheny.controller.ConversationActivity
import com.leilaarsene.hypheny.controller.LoginActivity
import com.leilaarsene.hypheny.data.User
import com.leilaarsene.hypheny.model.ApiService
import com.leilaarsene.hypheny.model.UserManager
import org.json.JSONObject

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    protected var activeUser: User? = null
    protected lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        apiService = ApiService(this)
        // Check if user is authenticated
        if (!isAuthenticated()) {
            // Redirect to login activity if not authenticated
            Log.d("ISAUTHENTICATEUSER", "NO")

            redirectToLogin()
        } else {
            Log.d("ISAUTHENTICATEUSER", "YES")
            initActiveUser()

        }

    }

    // Function to check if the user is authenticated
    private fun isAuthenticated(): Boolean {
        val token = sharedPreferences.getString("session_token", null)
        return token != null
    }

    // Function to log out and clear session token
    protected fun logout() {
        sharedPreferences.edit().remove("session_token").apply()
        sharedPreferences.edit().remove("active_user_data").apply()
        redirectToLogin()
    }

    // Redirect to login activity
    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun redirectToHome() {
        val intent = Intent(this, ConversationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun initActiveUser() {
        val activeUserDataJson = sharedPreferences.getString("active_user_data", null)
            ?.let { JSONObject(it) }
        activeUser = activeUserDataJson?.let { UserManager().getUserFromJSON(it) }
    }

    private fun updateActiveUser(userJsonData: String) {
        val activeUserDataJson = sharedPreferences.getString("active_user_data", null)
            ?.let { JSONObject(it) }
        activeUser = activeUserDataJson?.let { UserManager().getUserFromJSON(it) }
    }
}
