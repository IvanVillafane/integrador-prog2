let categories = [];
let products = [];
let users = [];
let cart = [];
let activeCategory = 'all';
let currentUser = null;

const API_BASE = '/api';

const userSelect = document.getElementById('userSelect');
const clientPanel = document.getElementById('clientPanel');
const adminPanel = document.getElementById('adminPanel');

const categoriesTabs = document.getElementById('categoriesTabs');
const productsGrid = document.getElementById('productsGrid');
const ordersList = document.getElementById('ordersList');
const cartItems = document.getElementById('cartItems');
const cartCount = document.getElementById('cartCount');
const cartSubtotal = document.getElementById('cartSubtotal');
const paymentMethod = document.getElementById('paymentMethod');
const checkoutBtn = document.getElementById('checkoutBtn');
const refreshHistoryBtn = document.getElementById('refreshHistoryBtn');

const prodCategoria = document.getElementById('prodCategoria');
const adminOrdersList = document.getElementById('adminOrdersList');
const createCategoryForm = document.getElementById('createCategoryForm');
const createProductForm = document.getElementById('createProductForm');
const refreshAdminHistoryBtn = document.getElementById('refreshAdminHistoryBtn');

const toastElement = document.getElementById('toast');

document.addEventListener('DOMContentLoaded', async () => {
    await loadUsers();
    await loadCategories();
    await loadProducts();
    
    userSelect.addEventListener('change', handleUserChange);
    refreshHistoryBtn.addEventListener('click', loadClientOrders);
    refreshAdminHistoryBtn.addEventListener('click', loadAdminOrders);
    checkoutBtn.addEventListener('click', handleCheckout);

    createCategoryForm.addEventListener('submit', handleCreateCategory);
    createProductForm.addEventListener('submit', handleCreateProduct);

    handleUserChange();
});

function showToast(message, type = 'success') {
    toastElement.className = `toast ${type}`;
    toastElement.innerHTML = `
        <i class="fa-solid ${type === 'success' ? 'fa-circle-check' : 'fa-circle-exclamation'}"></i>
        <span>${message}</span>
    `;
    toastElement.classList.remove('hidden');

    setTimeout(() => {
        toastElement.classList.add('hidden');
    }, 4000);
}

function handleUserChange() {
    const userId = parseInt(userSelect.value);
    currentUser = users.find(u => u.id === userId);

    if (!currentUser) return;

    if (currentUser.rol === 'ADMIN') {
        clientPanel.classList.add('hidden');
        clientPanel.classList.remove('active');
        adminPanel.classList.remove('hidden');
        adminPanel.classList.add('active');
        
        loadAdminOrders();
        populateCategorySelector();
    } else {
        adminPanel.classList.add('hidden');
        adminPanel.classList.remove('active');
        clientPanel.classList.remove('hidden');
        clientPanel.classList.add('active');

        cart = [];
        renderCart();
        renderProducts();
        loadClientOrders();
    }
}

async function loadUsers() {
    try {
        const res = await fetch(`${API_BASE}/usuarios`);
        users = await res.json();
        userSelect.innerHTML = users.map(u => 
            `<option value="${u.id}">${u.nombre} ${u.apellido} (${u.rol})</option>`
        ).join('');
    } catch (e) {
        showToast('Error al cargar usuarios', 'error');
    }
}

async function loadCategories() {
    try {
        const res = await fetch(`${API_BASE}/categorias`);
        categories = await res.json();
        
        let html = `<button class="tab-btn ${activeCategory === 'all' ? 'active' : ''}" data-category-id="all">Todos</button>`;
        html += categories.map(c => 
            `<button class="tab-btn ${activeCategory == c.id ? 'active' : ''}" data-category-id="${c.id}">${c.nombre}</button>`
        ).join('');
        
        categoriesTabs.innerHTML = html;
        populateCategorySelector();

        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
                activeCategory = btn.dataset.categoryId;
                renderProducts();
            });
        });
    } catch (e) {
        showToast('Error al cargar categorías', 'error');
    }
}

async function loadProducts() {
    try {
        const res = await fetch(`${API_BASE}/productos`);
        products = await res.json();
        renderProducts();
    } catch (e) {
        showToast('Error al cargar productos', 'error');
    }
}

function populateCategorySelector() {
    if (prodCategoria) {
        prodCategoria.innerHTML = categories.map(c => 
            `<option value="${c.id}">${c.nombre}</option>`
        ).join('');
    }
}

async function loadClientOrders() {
    if (!currentUser) return;
    try {
        const res = await fetch(`${API_BASE}/pedidos?usuarioId=${currentUser.id}`);
        const orders = await res.json();
        renderOrdersList(orders, ordersList, false);
    } catch (e) {
        showToast('Error al cargar pedidos del cliente', 'error');
    }
}

async function loadAdminOrders() {
    try {
        const res = await fetch(`${API_BASE}/pedidos`);
        const orders = await res.json();
        renderOrdersList(orders, adminOrdersList, true);
    } catch (e) {
        showToast('Error al cargar pedidos generales', 'error');
    }
}

function renderProducts() {
    const filtered = activeCategory === 'all' 
        ? products 
        : products.filter(p => p.categoriaId == activeCategory);

    if (filtered.length === 0) {
        productsGrid.innerHTML = `
            <div class="empty-cart" style="grid-column: 1 / -1; width: 100%;">
                <i class="fa-solid fa-cookie-bite"></i>
                <p>No hay productos disponibles en esta categoría</p>
            </div>
        `;
        return;
    }

    productsGrid.innerHTML = filtered.map(p => {
        const cartItem = cart.find(item => item.productoId === p.id);
        const cartQty = cartItem ? cartItem.cantidad : 0;
        const availableStock = p.stock - cartQty;
        const isOutOfStock = availableStock <= 0 || !p.disponible;

        let icon = 'fa-hamburger';
        if (p.categoriaNombre.toLowerCase().includes('pizza')) icon = 'fa-pizza-slice';
        else if (p.categoriaNombre.toLowerCase().includes('bebida') || p.nombre.toLowerCase().includes('coca')) icon = 'fa-wine-glass-empty';
        else if (p.categoriaNombre.toLowerCase().includes('postre') || p.nombre.toLowerCase().includes('torta') || p.nombre.toLowerCase().includes('tarta')) icon = 'fa-cookie-bite';
        else if (p.nombre.toLowerCase().includes('papa')) icon = 'fa-bowl-food';

        return `
            <div class="product-card">
                <div>
                    <div class="prod-img-container">
                        <i class="fa-solid ${icon}"></i>
                    </div>
                    <span class="prod-badge">${p.categoriaNombre}</span>
                    <div class="prod-title">${p.nombre}</div>
                    <div class="prod-desc">${p.descripcion}</div>
                </div>
                <div>
                    <div class="prod-footer">
                        <div>
                            <div class="prod-price">$${p.precio.toFixed(2)}</div>
                            <div class="prod-stock">Stock: ${availableStock} (${p.disponible ? 'Activo' : 'No Disp.'})</div>
                        </div>
                        <button class="add-cart-btn" onclick="addToCart(${p.id})" ${isOutOfStock ? 'disabled' : ''}>
                            <i class="fa-solid fa-plus"></i>
                        </button>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

function renderOrdersList(orders, targetContainer, isAdminView) {
    if (orders.length === 0) {
        targetContainer.innerHTML = `
            <div class="empty-cart">
                <i class="fa-solid fa-clock-rotate-left"></i>
                <p>No se registran pedidos</p>
            </div>
        `;
        return;
    }

    const ordersCopy = [...orders].reverse();

    targetContainer.innerHTML = ordersCopy.map(o => {
        const detailsHtml = o.detalles.map(d => `
            <div class="detail-row">
                <span>${d.productoNombre} x${d.cantidad}</span>
                <span>$${d.subtotal.toFixed(2)}</span>
            </div>
        `).join('');

        const totalItems = o.detalles.reduce((acc, d) => acc + d.cantidad, 0);
        
        let actionButtons = '';
        if (isAdminView && o.estado === 'PENDIENTE') {
            actionButtons = `
                <div class="admin-order-actions">
                    <button class="action-btn-small approve" onclick="changeOrderStatus(${o.id}, 'CONFIRMADO', event)">
                        <i class="fa-solid fa-circle-check"></i> Aprobar
                    </button>
                    <button class="action-btn-small reject" onclick="changeOrderStatus(${o.id}, 'CANCELADO', event)">
                        <i class="fa-solid fa-circle-xmark"></i> Rechazar
                    </button>
                </div>
            `;
        }

        return `
            <div class="order-card" onclick="toggleOrderDetails(${o.id}, ${isAdminView})">
                <div class="order-header">
                    <span class="order-id">Pedido #${o.id}</span>
                    <span class="order-status ${o.estado.toLowerCase()}">${o.estado}</span>
                </div>
                <div class="order-details-summary">
                    Cliente: <strong>${o.usuarioNombre}</strong>
                </div>
                <div class="order-footer">
                    <span>${o.fecha} | ${totalItems} ítem(s) | ${o.formaPago}</span>
                    <span class="order-total">$${o.total.toFixed(2)}</span>
                </div>
                <div class="order-items-detail" id="${isAdminView ? 'admin-' : ''}orderDetails-${o.id}">
                    ${detailsHtml}
                </div>
                ${actionButtons}
            </div>
        `;
    }).join('');
}

function toggleOrderDetails(orderId, isAdminView) {
    const prefix = isAdminView ? 'admin-' : '';
    const detailPanel = document.getElementById(`${prefix}orderDetails-${orderId}`);
    if (detailPanel) {
        detailPanel.classList.toggle('active');
    }
}

async function changeOrderStatus(orderId, newStatus, event) {
    if (event) event.stopPropagation();

    try {
        const response = await fetch(`${API_BASE}/pedidos/status`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                pedidoId: orderId,
                estado: newStatus
            })
        });

        const data = await response.json();

        if (response.ok) {
            showToast(`Pedido #${orderId} actualizado a ${newStatus}`);
            await loadProducts();
            await loadAdminOrders();
        } else {
            showToast(data.error || 'Error al actualizar el pedido.', 'error');
        }
    } catch (e) {
        showToast('Error de conexión con el servidor.', 'error');
    }
}

async function handleCreateCategory(event) {
    event.preventDefault();
    const nombre = document.getElementById('catNombre').value;
    const descripcion = document.getElementById('catDesc').value;

    try {
        const response = await fetch(`${API_BASE}/categorias`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ nombre, descripcion })
        });

        const data = await response.json();

        if (response.status === 201) {
            showToast('✓ Categoría creada exitosamente');
            createCategoryForm.reset();
            await loadCategories();
        } else {
            showToast(data.error || 'Error al crear la categoría.', 'error');
        }
    } catch (e) {
        showToast('Error al enviar la categoría.', 'error');
    }
}

async function handleCreateProduct(event) {
    event.preventDefault();
    const nombre = document.getElementById('prodNombre').value;
    const precio = parseFloat(document.getElementById('prodPrecio').value);
    const stock = parseInt(document.getElementById('prodStock').value);
    const categoriaId = parseInt(prodCategoria.value);
    const descripcion = document.getElementById('prodDesc').value;
    const imagen = document.getElementById('prodImagen').value;

    try {
        const response = await fetch(`${API_BASE}/productos`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                nombre,
                precio,
                stock,
                categoriaId,
                descripcion,
                imagen,
                disponible: true
            })
        });

        const data = await response.json();

        if (response.status === 201) {
            showToast('✓ Producto creado exitosamente');
            createProductForm.reset();
            await loadProducts();
        } else {
            showToast(data.error || 'Error al crear el producto.', 'error');
        }
    } catch (e) {
        showToast('Error al enviar el producto.', 'error');
    }
}

function addToCart(productId) {
    const product = products.find(p => p.id === productId);
    if (!product) return;

    const cartItem = cart.find(item => item.productoId === productId);
    const cartQty = cartItem ? cartItem.cantidad : 0;

    if (product.stock > cartQty) {
        if (cartItem) {
            cartItem.cantidad++;
        } else {
            cart.push({
                productoId: product.id,
                nombre: product.nombre,
                precio: product.precio,
                cantidad: 1,
                maxStock: product.stock
            });
        }
        renderCart();
        renderProducts();
        showToast(`Se agregó ${product.nombre} al carrito`);
    } else {
        showToast('No hay suficiente stock disponible.', 'error');
    }
}

function updateCartItemQty(productId, delta) {
    const cartItem = cart.find(item => item.productoId === productId);
    if (!cartItem) return;

    if (delta > 0 && cartItem.cantidad >= cartItem.maxStock) {
        showToast('Límite de stock alcanzado.', 'error');
        return;
    }

    cartItem.cantidad += delta;

    if (cartItem.cantidad <= 0) {
        removeFromCart(productId);
    } else {
        renderCart();
        renderProducts();
    }
}

function removeFromCart(productId) {
    const index = cart.findIndex(item => item.productoId === productId);
    if (index !== -1) {
        const name = cart[index].nombre;
        cart.splice(index, 1);
        renderCart();
        renderProducts();
        showToast(`Se eliminó ${name} del carrito`, 'error');
    }
}

function renderCart() {
    if (cart.length === 0) {
        cartItems.innerHTML = `
            <div class="empty-cart">
                <i class="fa-solid fa-basket-shopping"></i>
                <p>El carrito está vacío</p>
            </div>
        `;
        cartCount.textContent = '0';
        cartSubtotal.textContent = '$0.00';
        checkoutBtn.disabled = true;
        return;
    }

    cartItems.innerHTML = cart.map(item => `
        <div class="cart-item">
            <div class="item-info">
                <div class="item-title">${item.nombre}</div>
                <div class="item-price">$${item.precio.toFixed(2)} c/u</div>
            </div>
            <div class="item-controls">
                <button class="qty-btn" onclick="updateCartItemQty(${item.productoId}, -1)">-</button>
                <span class="item-qty">${item.cantidad}</span>
                <button class="qty-btn" onclick="updateCartItemQty(${item.productoId}, 1)">+</button>
                <button class="remove-item-btn" onclick="removeFromCart(${item.productoId})">
                    <i class="fa-solid fa-trash-can"></i>
                </button>
            </div>
        </div>
    `).join('');

    const subtotal = cart.reduce((acc, item) => acc + (item.precio * item.cantidad), 0);
    const count = cart.reduce((acc, item) => acc + item.cantidad, 0);

    cartCount.textContent = count;
    cartSubtotal.textContent = `$${subtotal.toFixed(2)}`;
    checkoutBtn.disabled = false;
}

async function handleCheckout() {
    if (!currentUser) return;

    const items = cart.map(item => ({
        productoId: item.productoId,
        cantidad: item.cantidad
    }));

    const body = {
        usuarioId: currentUser.id,
        formaPago: paymentMethod.value,
        items: items
    };

    checkoutBtn.disabled = true;
    checkoutBtn.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin"></i> Procesando...';

    try {
        const response = await fetch(`${API_BASE}/pedidos`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(body)
        });

        const data = await response.json();

        if (response.status === 201) {
            showToast('✓ ¡Pedido creado con éxito!');
            cart = [];
            renderCart();
            await loadProducts();
            await loadClientOrders();
        } else {
            showToast(data.error || 'Error al procesar el pedido.', 'error');
        }
    } catch (e) {
        showToast('Error de conexión con el servidor.', 'error');
    } finally {
        checkoutBtn.disabled = cart.length === 0;
        checkoutBtn.innerHTML = 'Confirmar Pedido <i class="fa-solid fa-chevron-right"></i>';
    }
}
