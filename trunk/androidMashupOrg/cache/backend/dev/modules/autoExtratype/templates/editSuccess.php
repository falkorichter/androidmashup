<?php use_helper('Object', 'Validation', 'ObjectAdmin', 'I18N', 'Date') ?>

<?php use_stylesheet('/sf/sf_admin/css/main') ?>

<div id="sf_admin_container">

<h1><?php echo __('edit extratype', 
array()) ?></h1>

<div id="sf_admin_header">
<?php include_partial('extratype/edit_header', array('extratype' => $extratype)) ?>
</div>

<div id="sf_admin_content">
<?php include_partial('extratype/edit_messages', array('extratype' => $extratype, 'labels' => $labels)) ?>
<?php include_partial('extratype/edit_form', array('extratype' => $extratype, 'labels' => $labels)) ?>
</div>

<div id="sf_admin_footer">
<?php include_partial('extratype/edit_footer', array('extratype' => $extratype)) ?>
</div>

</div>
