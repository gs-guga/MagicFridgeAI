UPDATE food_item
SET categoria = 'OUTROS'
WHERE categoria IS NULL
   OR categoria NOT IN ('FRUTAS', 'LEGUMES_E_VERDURAS', 'LATICINIOS', 'CARNES',
                        'OVOS', 'GRAOS_E_CEREAIS', 'BEBIDAS', 'TEMPEROS', 'OUTROS');