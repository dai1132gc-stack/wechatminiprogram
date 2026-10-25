import { reactive, ref, type Ref } from 'vue'
import type { PageData, PageParams } from '@/types/api'

/**
 * 列表页通用逻辑（设计方案 §2.4 统一范式）：
 * 点「查询」才请求（不做输入即查）、重置回第 1 页、空态区分无数据/无匹配。
 * fetcher 由页面注入，返回 PageData。
 */
/** 查询条件 + 必填分页字段（由 useTable 注入默认值） */
export type TableQuery<Q> = Q & Required<Pick<PageParams, 'pageNum' | 'pageSize'>>

export function useTable<T, Q extends object>(
  fetcher: (query: TableQuery<Q>) => Promise<PageData<T> | null>,
  defaultQuery: Q,
  defaultPageSize = 10,
) {
  const query = reactive({ ...defaultQuery, pageNum: 1, pageSize: defaultPageSize }) as unknown as TableQuery<Q>
  const rows: Ref<T[]> = ref([])
  const total = ref(0)
  const loading = ref(false)
  const searched = ref(false) // 区分「无数据」与「无匹配结果」空态
  query.pageNum = 1
  query.pageSize = defaultPageSize

  async function fetchList(): Promise<void> {
    loading.value = true
    try {
      const data = await fetcher({ ...query })
      rows.value = data?.list ?? []
      total.value = data?.total ?? 0
      searched.value = true
    } finally {
      loading.value = false
    }
  }

  function search(): void {
    query.pageNum = 1
    fetchList()
  }

  function reset(): void {
    Object.assign(query, defaultQuery, { pageNum: 1, pageSize: query.pageSize })
    fetchList()
  }

  function onPageChange(pageNum: number): void {
    query.pageNum = pageNum
    fetchList()
  }

  function onSizeChange(pageSize: number): void {
    query.pageSize = pageSize
    query.pageNum = 1
    fetchList()
  }

  return { query, rows, total, loading, searched, fetchList, search, reset, onPageChange, onSizeChange }
}
