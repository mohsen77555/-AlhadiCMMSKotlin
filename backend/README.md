# الخادم الخلفي للتطبيق

خادم مستقل مبني بلغة Kotlin، ويعمل مع قاعدة PostgreSQL. المرحلة الأولى توفر:

- فحص الجاهزية والحالة.
- تسجيل الدخول باستخدام اسم مستخدم وكلمة مرور.
- رموز وصول قصيرة المدة ورموز تحديث دوّارة قابلة للإلغاء.
- تشفير كلمات المرور باستخدام PBKDF2 مع ملح مستقل لكل مستخدم.
- حماية من محاولات تسجيل الدخول المتكررة.
- إنشاء أول مدير من متغيرات البيئة فقط عند خلو قاعدة البيانات.
- سجل تدقيق أساسي لعمليات الدخول والجلسات.
- تشغيل محلي أو على خادم باستخدام Docker Compose.

## التشغيل

```bash
cp .env.backend.example .env.backend
# عدّل جميع كلمات المرور والأسرار داخل الملف

docker compose --env-file .env.backend -f docker-compose.backend.yml up --build -d
```

فحص الجاهزية:

```bash
curl http://localhost:8080/health/ready
```

تسجيل الدخول:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"YOUR_PASSWORD","organizationCode":"DEFAULT"}'
```

## أهم متغيرات البيئة

- `DB_URL`, `DB_USER`, `DB_PASSWORD`
- `JWT_SECRET` بطول لا يقل عن 32 حرفاً
- `BOOTSTRAP_ADMIN_USERNAME`, `BOOTSTRAP_ADMIN_PASSWORD`
- `ORGANIZATION_CODE`, `ORGANIZATION_NAME`
- `CORS_HOSTS`

لا توجد بيانات دخول افتراضية داخل الكود. بعد إنشاء أول مدير، لا يعاد إنشاؤه عند إعادة تشغيل الخادم.
