
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dhkim.gamsahanilsang.R
import com.dhkim.gamsahanilsang.presentation.viewModel.AuthState
import com.dhkim.gamsahanilsang.presentation.viewModel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit, // 로그인 성공 시 호출될 콜백
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    // 구글 로그인 결과 처리를 위한 ActivityResultLauncher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            // 구글 로그인 성공
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken // Google ID 토큰

            // Firebase에 구글 계정으로 인증
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            viewModel.signInWithGoogle(credential)

        } catch (e: ApiException) {
            // 구글 로그인 실패
            Log.e("LoginScreen", "Google sign in failed", e)
            // TODO: 사용자에게 로그인 실패 메시지 표시
        }
    }

    // AuthViewModel의 상태를 관찰
    val authState by viewModel.authState.collectAsState()

    // 로그인 상태 변화에 따라 화면 전환
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            // 로그인 성공 상태이면 onLoginSuccess 콜백 호출
            onLoginSuccess()
        }
        // TODO: 에러 상태 처리 (토스트 메시지 등)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "감사한 일상",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // 구글 로그인 버튼
        Button(
            onClick = {
                // 구글 로그인 옵션 설정
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.search)) // 수정해야함
                    .requestEmail()
                    .build()

                // GoogleSignInClient 가져오기
                val googleSignInClient = GoogleSignIn.getClient(context, gso)

                // 구글 로그인 인텐트 실행
                launcher.launch(googleSignInClient.signInIntent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 구글 아이콘 (drawable에 ic_google.xml 추가 필요)
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("구글 계정으로 로그인")
            }
        }

        // 로딩 상태 표시
        if (authState is AuthState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // 에러 메시지 표시
        if (authState is AuthState.Error) {
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
