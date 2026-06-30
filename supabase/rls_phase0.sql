-- Phase 0 RLS: authenticated users only (tightened to roles in Phase 4).

-- Profiles: one row per auth user, holds the role used by later RLS phases.
create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  username text unique,
  role text not null default 'Requester',
  craft text default '',
  groups text[] default '{}',
  created_at timestamptz default now()
);
alter table public.profiles enable row level security;
create policy "profiles self read"  on public.profiles for select to authenticated using (true);
create policy "profiles self write" on public.profiles for all to authenticated using (auth.uid() = id) with check (auth.uid() = id);

alter table public.asset_bom_headers enable row level security;
create policy "asset_bom_headers auth all" on public.asset_bom_headers for all to authenticated using (true) with check (true);
alter table public.asset_bom_items enable row level security;
create policy "asset_bom_items auth all" on public.asset_bom_items for all to authenticated using (true) with check (true);
alter table public.asset_characteristics enable row level security;
create policy "asset_characteristics auth all" on public.asset_characteristics for all to authenticated using (true) with check (true);
alter table public.asset_documents enable row level security;
create policy "asset_documents auth all" on public.asset_documents for all to authenticated using (true) with check (true);
alter table public.assets enable row level security;
create policy "assets auth all" on public.assets for all to authenticated using (true) with check (true);
alter table public.asset_installations enable row level security;
create policy "asset_installations auth all" on public.asset_installations for all to authenticated using (true) with check (true);
alter table public.asset_movements enable row level security;
create policy "asset_movements auth all" on public.asset_movements for all to authenticated using (true) with check (true);
alter table public.asset_status_history enable row level security;
create policy "asset_status_history auth all" on public.asset_status_history for all to authenticated using (true) with check (true);
alter table public.audit_log enable row level security;
create policy "audit_log auth all" on public.audit_log for all to authenticated using (true) with check (true);
alter table public.capa_actions enable row level security;
create policy "capa_actions auth all" on public.capa_actions for all to authenticated using (true) with check (true);
alter table public.functional_locations enable row level security;
create policy "functional_locations auth all" on public.functional_locations for all to authenticated using (true) with check (true);
alter table public.inventory_transactions enable row level security;
create policy "inventory_transactions auth all" on public.inventory_transactions for all to authenticated using (true) with check (true);
alter table public.maintenance_notifications enable row level security;
create policy "maintenance_notifications auth all" on public.maintenance_notifications for all to authenticated using (true) with check (true);
alter table public.measurement_readings enable row level security;
create policy "measurement_readings auth all" on public.measurement_readings for all to authenticated using (true) with check (true);
alter table public.measuring_points enable row level security;
create policy "measuring_points auth all" on public.measuring_points for all to authenticated using (true) with check (true);
alter table public.org_units enable row level security;
create policy "org_units auth all" on public.org_units for all to authenticated using (true) with check (true);
alter table public.pm_checklist_items enable row level security;
create policy "pm_checklist_items auth all" on public.pm_checklist_items for all to authenticated using (true) with check (true);
alter table public.preventive_maintenance enable row level security;
create policy "preventive_maintenance auth all" on public.preventive_maintenance for all to authenticated using (true) with check (true);
alter table public.purchase_orders enable row level security;
create policy "purchase_orders auth all" on public.purchase_orders for all to authenticated using (true) with check (true);
alter table public.purchase_order_lines enable row level security;
create policy "purchase_order_lines auth all" on public.purchase_order_lines for all to authenticated using (true) with check (true);
alter table public.serial_numbers enable row level security;
create policy "serial_numbers auth all" on public.serial_numbers for all to authenticated using (true) with check (true);
alter table public.serial_number_movements enable row level security;
create policy "serial_number_movements auth all" on public.serial_number_movements for all to authenticated using (true) with check (true);
alter table public.serial_number_profiles enable row level security;
create policy "serial_number_profiles auth all" on public.serial_number_profiles for all to authenticated using (true) with check (true);
alter table public.spare_parts enable row level security;
create policy "spare_parts auth all" on public.spare_parts for all to authenticated using (true) with check (true);
alter table public.suppliers enable row level security;
create policy "suppliers auth all" on public.suppliers for all to authenticated using (true) with check (true);
alter table public.task_lists enable row level security;
create policy "task_lists auth all" on public.task_lists for all to authenticated using (true) with check (true);
alter table public.task_list_operations enable row level security;
create policy "task_list_operations auth all" on public.task_list_operations for all to authenticated using (true) with check (true);
alter table public.users enable row level security;
create policy "users auth all" on public.users for all to authenticated using (true) with check (true);
alter table public.warehouses enable row level security;
create policy "warehouses auth all" on public.warehouses for all to authenticated using (true) with check (true);
alter table public.work_order_confirmations enable row level security;
create policy "work_order_confirmations auth all" on public.work_order_confirmations for all to authenticated using (true) with check (true);
alter table public.work_orders enable row level security;
create policy "work_orders auth all" on public.work_orders for all to authenticated using (true) with check (true);
alter table public.work_order_history enable row level security;
create policy "work_order_history auth all" on public.work_order_history for all to authenticated using (true) with check (true);
alter table public.work_order_materials enable row level security;
create policy "work_order_materials auth all" on public.work_order_materials for all to authenticated using (true) with check (true);
alter table public.work_order_operations enable row level security;
create policy "work_order_operations auth all" on public.work_order_operations for all to authenticated using (true) with check (true);
alter table public.work_order_photos enable row level security;
create policy "work_order_photos auth all" on public.work_order_photos for all to authenticated using (true) with check (true);
alter table public.work_permits enable row level security;
create policy "work_permits auth all" on public.work_permits for all to authenticated using (true) with check (true);
