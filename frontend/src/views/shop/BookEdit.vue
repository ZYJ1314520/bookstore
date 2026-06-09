<template>
  <div class="book-edit">
    <h2>{{ isEdit ? '编辑图书' : '新增图书' }}</h2>
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" style="max-width: 800px">
      <el-form-item label="书名" prop="title">
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="作者" prop="author">
        <el-input v-model="form.author" />
      </el-form-item>
      <el-form-item label="ISBN">
        <el-input v-model="form.isbn" />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="form.categoryId" placeholder="请选择分类">
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="售价" prop="price">
        <el-input-number v-model="form.price" :precision="2" :min="0" />
      </el-form-item>
      <el-form-item label="原价">
        <el-input-number v-model="form.originalPrice" :precision="2" :min="0" />
      </el-form-item>
      <el-form-item label="库存" prop="stock">
        <el-input-number v-model="form.stock" :min="0" />
      </el-form-item>
      <el-form-item label="出版社">
        <el-input v-model="form.publisher" />
      </el-form-item>
      <el-form-item label="出版日期">
        <el-date-picker v-model="form.publishDate" type="date" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item label="封面图片">
        <el-upload
          class="cover-uploader"
          :http-request="customCoverUpload"
          :show-file-list="false"
          :before-upload="beforeUpload"
        >
          <img v-if="form.cover" :src="imgUrl(form.cover)" class="cover-img" />
          <el-icon v-else class="cover-placeholder"><Plus /></el-icon>
        </el-upload>
      </el-form-item>
      <el-form-item label="简介">
        <el-input v-model="form.description" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          {{ isEdit ? '保存修改' : '立即添加' }}
        </el-button>
        <el-button @click="$router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request, { imgUrl } from '@/api'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const formRef = ref()
const loading = ref(false)
const categories = ref([])

const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('shopToken') || ''}`
}))

const form = ref({
  title: '',
  author: '',
  isbn: '',
  categoryId: null,
  price: 0,
  originalPrice: 0,
  stock: 0,
  publisher: '',
  publishDate: '',
  cover: '',
  description: ''
})

const rules = {
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  return true
}

const customCoverUpload = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)
  try {
    const res = await request.post('/api/upload', formData)
    console.log('upload response:', JSON.stringify(res.data))
    const data = res.data
    if (data.code === 200) {
      form.value.cover = data.data
      ElMessage.success('封面上传成功')
    } else {
      ElMessage.error(data.message || '上传失败')
    }
  } catch (e) {
    console.error('upload error:', e.response?.data || e.message)
    ElMessage.error('封面上传失败: ' + (e.response?.data?.message || e.message || '网络错误'))
  }
}


onMounted(async () => {
  // 获取分类
  const catRes = await request.get('/api/public/categories')
  if (catRes.data.code === 200) categories.value = catRes.data.data

  // 编辑时加载图书信息
  if (isEdit.value) {
    const bookRes = await request.get(`/api/shop/books/${route.params.id}`)
    if (bookRes.data.code === 200) {
      const book = bookRes.data.data
      form.value = {
        title: book.title || '',
        author: book.author || '',
        isbn: book.isbn || '',
        categoryId: book.categoryId || null,
        price: book.price || 0,
        originalPrice: book.originalPrice || 0,
        stock: book.stock || 0,
        publisher: book.publisher || '',
        publishDate: book.publishDate || '',
        cover: book.cover || '',
        description: book.description || ''
      }
    }
  }
})

const handleSubmit = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const submitData = { ...form.value }

    if (isEdit.value) {
      await request.put(`/api/shop/books/${route.params.id}`, submitData)
      ElMessage.success('修改成功')
    } else {
      await request.post('/api/shop/books', submitData)
      ElMessage.success('添加成功')
    }
    router.push('/shop/books')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.book-edit {
  background: white;
  padding: 30px;
  border-radius: 8px;
}
h2 {
  margin-bottom: 20px;
}
.cover-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 178px;
  height: 178px;
}
.cover-uploader:hover {
  border-color: #409eff;
}
.cover-img {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}
.cover-placeholder {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
