import { usePermissionStore } from '../stores/permission'

export default {
  mounted(el, binding) {
    const permStore = usePermissionStore()
    const requiredPerm = binding.value
    if (requiredPerm && !permStore.hasPermission(requiredPerm)) {
      el.parentNode?.removeChild(el)
    }
  }
}