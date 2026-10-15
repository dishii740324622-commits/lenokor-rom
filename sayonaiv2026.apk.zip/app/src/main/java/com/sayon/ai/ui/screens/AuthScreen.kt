package com.sayon.ai.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sayon.ai.data.SayonRepository
import com.sayon.ai.ui.theme.*

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Futuristic Glowing S Logo Background Subtle Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw subtle background circuit lines
            val path = Path().apply {
                moveTo(size.width * 0.1f, size.height * 0.2f)
                lineTo(size.width * 0.3f, size.height * 0.2f)
                lineTo(size.width * 0.4f, size.height * 0.3f)
            }
            drawPath(path, color = Color(0x1100E5FF), style = Stroke(width = 2f))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Logo & Title Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Futuristic Glowing S Logo Component
                SayonGlowingLogo(modifier = Modifier.size(110.dp))

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (isRegisterMode) "إنشاء حساب جديد" else "تسجيل الدخول",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            }

            // Input Fields Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(visible = isRegisterMode) {
                    Column {
                        // Name Input
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = { Text("الاسم الكامل", color = TextHint, fontSize = 14.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .height(56.dp)
                                .background(GlassInputBg, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GlowCyan,
                                unfocusedBorderColor = DarkGlassBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        // Username Input
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            placeholder = { Text("اسم المستخدم (@username)", color = TextHint, fontSize = 14.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .height(56.dp)
                                .background(GlassInputBg, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GlowCyan,
                                unfocusedBorderColor = DarkGlassBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                }

                // Email Input Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("البريد الإلكتروني", color = TextHint, fontSize = 14.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(GlassInputBg, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GlowCyan,
                        unfocusedBorderColor = DarkGlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password Input Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("كلمة المرور", color = TextHint, fontSize = 14.sp) },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Password",
                                tint = TextSecondary
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(GlassInputBg, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GlowCyan,
                        unfocusedBorderColor = DarkGlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Login/Register Main Button
                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "يرجى كتابة البريد الإلكتروني وكلمة المرور"
                            return@Button
                        }
                        val uName = if (username.isBlank()) "@" + email.substringBefore("@") else username
                        val n = if (name.isBlank()) email.substringBefore("@") else name
                        SayonRepository.login(email = email, name = n, username = uName)
                        onLoginSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(1.dp, GlowWhite.copy(alpha = 0.8f), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF161922)
                    )
                ) {
                    Text(
                        text = if (isRegisterMode) "إنشاء حساب" else "تسجيل الدخول",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                if (!isRegisterMode) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "نسيت كلمة المرور؟",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier
                            .clickable { /* Forgotten password */ }
                            .padding(4.dp)
                    )
                }

                errorMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = msg, color = ErrorRed, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Divider Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = DarkGlassBorder)
                    Text(
                        text = "  أو قم بإنشاء حساب باستخدام  ",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = DarkGlassBorder)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Google Sign In Pill Button (Google logo + White Pill Design)
                Button(
                    onClick = {
                        // Google Sign In action: Redirects user to Google chooser / logs in
                        SayonRepository.login(
                            email = "user.google@gmail.com",
                            name = "مستخدم Google",
                            username = "@google_user"
                        )
                        onLoginSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(4.dp, RoundedCornerShape(25.dp)),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Google G Icon representation
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4285F4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Sign in with Google",
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Toggle Register/Login Mode Button
                Button(
                    onClick = {
                        isRegisterMode = !isRegisterMode
                        errorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(1.dp, DarkGlassBorder, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(
                        text = if (isRegisterMode) "تسجيل الدخول باستخدام حساب حالي" else "إنشاء حساب جديد",
                        fontSize = 15.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Footer Copyright Section (Reference Image 3: © 2026 Sayon AI)
            Text(
                text = "© 2026 Sayon AI",
                fontSize = 12.sp,
                color = TextHint,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

/**
 * Custom Glowing Neon S Logo matching Image 3 header.
 */
@Composable
fun SayonGlowingLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = size / 2f
            val radius = size.minDimension / 2.4f

            // Outer Neon Glow Circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x6600E5FF), Color.Transparent),
                    center = center,
                    radius = radius * 1.5f
                ),
                radius = radius * 1.4f
            )

            // Stylized S Circuit Curve
            val path = Path().apply {
                moveTo(center.width + radius * 0.5f, center.height - radius * 0.6f)
                cubicTo(
                    center.width - radius * 0.8f, center.height - radius * 0.8f,
                    center.width - radius * 0.8f, center.height,
                    center.width, center.height
                )
                cubicTo(
                    center.width + radius * 0.8f, center.height,
                    center.width + radius * 0.8f, center.height + radius * 0.8f,
                    center.width - radius * 0.5f, center.height + radius * 0.6f
                )
            }

            drawPath(
                path = path,
                color = Color(0xFF00E5FF),
                style = Stroke(width = 6f)
            )

            // Nodes on circuit
            drawCircle(Color.White, radius = 6f, center = center)
            drawCircle(Color(0xFF00E5FF), radius = 8f, center = path.getBounds().topLeft)
            drawCircle(Color(0xFF00E5FF), radius = 8f, center = path.getBounds().bottomRight)
        }
    }
}
