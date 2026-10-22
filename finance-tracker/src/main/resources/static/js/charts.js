/* charts.js — renders Chart.js charts on the dashboard */
(function () {
    'use strict';

    // Monthly bar chart
    const monthlyCtx = document.getElementById('monthlyChart');
    if (monthlyCtx && typeof monthLabels !== 'undefined') {
        new Chart(monthlyCtx, {
            type: 'bar',
            data: {
                labels: monthLabels,
                datasets: [
                    {
                        label: 'Income',
                        data: monthlyIncome,
                        backgroundColor: 'rgba(16,185,129,.75)',
                        borderRadius: 4
                    },
                    {
                        label: 'Expenses',
                        data: monthlyExpense,
                        backgroundColor: 'rgba(239,68,68,.75)',
                        borderRadius: 4
                    }
                ]
            },
            options: {
                responsive: true,
                plugins: { legend: { position: 'bottom', labels: { font: { size: 12 } } } },
                scales: {
                    y: { beginAtZero: true, ticks: { callback: v => '$' + v.toLocaleString() } }
                }
            }
        });
    }

    // Category doughnut chart
    const categoryCtx = document.getElementById('categoryChart');
    if (categoryCtx && typeof categoryLabels !== 'undefined' && categoryLabels.length) {
        const palette = ['#ef4444','#f97316','#eab308','#22c55e','#06b6d4','#6366f1','#ec4899','#14b8a6','#f43f5e','#84cc16'];
        new Chart(categoryCtx, {
            type: 'doughnut',
            data: {
                labels: categoryLabels,
                datasets: [{
                    data: categoryData,
                    backgroundColor: palette.slice(0, categoryLabels.length),
                    borderWidth: 2,
                    borderColor: '#fff'
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'bottom', labels: { font: { size: 12 }, boxWidth: 12 } },
                    tooltip: {
                        callbacks: {
                            label: ctx => ' $' + parseFloat(ctx.parsed).toLocaleString(undefined, {minimumFractionDigits: 2})
                        }
                    }
                }
            }
        });
    } else if (categoryCtx) {
        categoryCtx.parentElement.innerHTML += '<p style="text-align:center;color:#64748b;font-size:.85rem;margin-top:2rem">No expense data this month.</p>';
    }
})();
